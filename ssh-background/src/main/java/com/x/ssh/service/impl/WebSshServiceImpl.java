package com.x.ssh.service.impl;

import ch.ethz.ssh2.ConnectionInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.x.ssh.bus.constant.WebSshConstantPool;
import com.x.ssh.entity.SSHConnectInfo;
import com.x.ssh.entity.WebSshData;
import com.x.ssh.service.WebSshService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ProjectName: webSsh
 * @Package: com.x.ssh.service.impl
 * @ClassName: WebSshServiceImpl
 * @Author: Yao
 * @Description:
 * @CreateDate: 2020/12/29 14:47
 * @Version:
 */
@Service
public class WebSshServiceImpl implements WebSshService {

    /**
     * 存放ssh连接信息的map
     */
    private static Map<String, Object> sshMap = new ConcurrentHashMap<>();

    private Logger logger = LoggerFactory.getLogger(WebSshServiceImpl.class);

    //线程池, ThreadPoolExecutor
    private ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     *初始化连接
     * @param socketSession
     */
    @Override
    public void initConnection(WebSocketSession socketSession) {
        JSch jSch = new JSch();
        SSHConnectInfo sshConnectInfo = new SSHConnectInfo();
        sshConnectInfo.setjSch(jSch);
        sshConnectInfo.setWebSocketSession(socketSession);
        String uuid = String.valueOf(socketSession.getAttributes().get(WebSshConstantPool.USER_UUID_KEY));
        //将ssh连接信息放入map中
        sshMap.put(uuid, sshConnectInfo);
    }

    /**
     *处理客户端发送的数据
     * @param buffer
     * @param session
     */
    @Override
    public void receiveHandle(String buffer, WebSocketSession session) {
        ObjectMapper objectMapper = new ObjectMapper();
        WebSshData webSshData = null;
        try {
            webSshData = objectMapper.readValue(buffer, WebSshData.class);
        } catch (IOException ioe) {
            logger.error("json convert error");
            logger.error("exception message:{}", ioe.getMessage());
            return;
        }
        String userId = String.valueOf(session.getAttributes().get(WebSshConstantPool.USER_UUID_KEY));
        if (WebSshConstantPool.WEB_SSH_OPERATE_CONNECT.equals(webSshData.getOperate())) {
            //找到刚才存储的ssh连接对象
            SSHConnectInfo sshConnectInfo = (SSHConnectInfo) sshMap.get(userId);
            //启动线程异步处理
            WebSshData finalWebSshData = webSshData;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        connectToSsh(sshConnectInfo, finalWebSshData, session);
                    }catch (JSchException | IOException e) {
                        logger.error("webSsh connect exception");
                        logger.error("error message:{}", e.getMessage());
                        close(session);
                    }
                }
            });
        } else if (WebSshConstantPool.WEB_SSH_OPERATE_COMMAND.equals(webSshData.getOperate())) {
            String command = webSshData.getCommand();
            SSHConnectInfo sshConnectInfo = (SSHConnectInfo) sshMap.get(userId);
            if (sshConnectInfo != null) {
                try {
                    transToSsh(sshConnectInfo.getChannel(), command);
                } catch (IOException e) {
                    logger.error("webSsh connect exception");
                    logger.error("error message:{}", e.getMessage());
                    close(session);
                }
            }
        }else {
            logger.error("Unsupported Operations");
            close(session);
        }
    }


    /**
     *
     * @param socketSession
     * @param buffer
     * @throws IOException
     */
    @Override
    public void sendMessage(WebSocketSession socketSession, byte[] buffer) throws IOException {
        socketSession.sendMessage(new TextMessage(buffer));
    }

    /**
     *
     * @param socketSession
     */
    @Override
    public void close(WebSocketSession socketSession) {
        String userId = String.valueOf(socketSession.getAttributes().get(WebSshConstantPool.USER_UUID_KEY));
        SSHConnectInfo sshConnectInfo = (SSHConnectInfo) sshMap.get(userId);
        if (sshConnectInfo != null) {
            //断开连接
            if (sshConnectInfo.getChannel() != null) {
                sshConnectInfo.getChannel().disconnect();
            }
            sshMap.remove(userId);
        }
    }

    /**
     *使用jsch连接终端
     * @param sshConnectInfo
     * @param webSshData
     * @param socketSession
     * @throws JSchException
     */
    private void connectToSsh(SSHConnectInfo sshConnectInfo, WebSshData webSshData, WebSocketSession socketSession) throws JSchException, IOException {
        Session session = null;
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        //获取jsch的会话
        session = sshConnectInfo.getjSch().getSession(webSshData.getUserName(), webSshData.getHost(), webSshData.getPort());
        session.setConfig(config);
        //设置密码
        session.setPassword(webSshData.getPassword());
        //连接，超时时间30s
        session.connect(30000);
        //开启shell通道
        Channel shell = session.openChannel("shell");
        //通道连接超时时间3s
        shell.connect(3000);
        //设置channel
        sshConnectInfo.setChannel(shell);
        //转发消息
        transToSsh(shell, "\r");
        //读取终端返回的信息流
        InputStream inputStream = shell.getInputStream();
        try{
            //循环读取
            byte[] bytes = new byte[1024];
            int i = 0;
            //如果没有数据来，线程会一直阻塞在这个地方等待数据
            while ((i = inputStream.read(bytes)) != -1){
                sendMessage(socketSession, Arrays.copyOfRange(bytes, 0, i));
            }
        }finally {
            //断开连接后关闭会话
            session.disconnect();
            shell.disconnect();
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * 将消息转发到终端
     * @param channel
     * @param command
     * @throws IOException
     */
    private void transToSsh(Channel channel, String command) throws IOException {
        if (Objects.nonNull(channel)) {
            OutputStream outputStream = channel.getOutputStream();
            outputStream.write(command.getBytes());
            outputStream.flush();
        }
    }















}
