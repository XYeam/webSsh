package com.x.ssh.bus.config;

import com.x.ssh.aop.WebSocketInterceptor;
import com.x.ssh.aop.WebSshWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @ProjectName: webSsh
 * @Package: com.x.ssh.bus.config
 * @ClassName: WebSshWebSocketConfig
 * @Author: Yao
 * @Description: webSocket 配置
 * @CreateDate: 2020/12/29 14:01
 * @Version:
 */
@Configuration
@EnableWebSocket
public class WebSshWebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private WebSshWebSocketHandler webSshWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        //Socket 通道
        //指定处理器和路径，并设置跨域
        webSocketHandlerRegistry.addHandler(webSshWebSocketHandler, "com.x.ssh.aop.WebSshWebSocketHandler")
                .addInterceptors(new WebSocketInterceptor())
                .setAllowedOrigins("*");
    }
}
