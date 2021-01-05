package com.x.ssh.service;

import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * @ProjectName: webSsh
 * @Package: com.x.ssh.service
 * @ClassName: WebSshService
 * @Author: Yao
 * @Description:
 *                  1、建立连接
 *                  2、处理命令
 *                  3、返回数据
 *                  4、断开连接
 *
 * @CreateDate: 2020/12/29 14:31
 * @Version:
 */
public interface WebSshService {

    /**
     * 初始化ssh连接
     * @param socketSession
     */
    public void initConnection(WebSocketSession socketSession);

    /**
     * 处理客户端发的命令
     * @param buffer
     * @param session
     */
    public void receiveHandle(String buffer, WebSocketSession session);

    /**
     * 将结果数据返回
     * @param socketSession
     * @param buffer
     * @throws IOException
     */
    public void sendMessage(WebSocketSession socketSession, byte[] buffer) throws IOException;

    /**
     * 关闭连接
     * @param socketSession
     */
    public void close(WebSocketSession socketSession);
}
