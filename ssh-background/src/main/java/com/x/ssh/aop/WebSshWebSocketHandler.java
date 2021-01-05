package com.x.ssh.aop;

import com.x.ssh.bus.constant.WebSshConstantPool;
import com.x.ssh.service.WebSshService;
import org.apache.tomcat.util.bcel.classfile.ConstantPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

/**
 * @ProjectName: webSsh
 * @Package: com.x.ssh.aop
 * @ClassName: WebSshWebSocketHandler
 * @Author: Yao
 * @Description: handler
 * @CreateDate: 2020/12/29 14:04
 * @Version:
 */
@Component
public class WebSshWebSocketHandler implements WebSocketHandler {

    @Autowired
    private WebSshService webSshService;

    private Logger logger = LoggerFactory.getLogger(WebSshWebSocketHandler.class);


    /**
     * 用户连接上WebSocket的回调
     * @param webSocketSession
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        logger.info("用户:{},连接WebSsh", webSocketSession.getAttributes().get(WebSshConstantPool.USER_UUID_KEY));
        //初始化连接
        webSshService.initConnection(webSocketSession);
    }

    /**
     * 收到消息的回调
     * @param webSocketSession
     * @param webSocketMessage
     * @throws Exception
     */
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        if (webSocketMessage instanceof TextMessage) {
            logger.info("user:{},sending command:{}", webSocketSession.getAttributes(), webSocketMessage.toString());
            //调用service接收
            webSshService.receiveHandle(((TextMessage)webSocketMessage).getPayload(), webSocketSession);
        } else if (webSocketMessage instanceof BinaryMessage) {

        }else {
            System.out.println("Unexpected WebSocket message type:" + webSocketMessage);
        }
    }

    /**
     * 出现错误的回调
     * @param webSocketSession
     * @param throwable
     * @throws Exception
     */
    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        logger.error("data send error");
    }

    /**
     * 连接关闭的回调
     * @param webSocketSession
     * @param closeStatus
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        logger.info("user:{},disconnecting", String.valueOf(webSocketSession.getAttributes().get(WebSshConstantPool.USER_UUID_KEY)));
        webSshService.close(webSocketSession);
    }

    /**
     *
     * @return boolean
     */
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
