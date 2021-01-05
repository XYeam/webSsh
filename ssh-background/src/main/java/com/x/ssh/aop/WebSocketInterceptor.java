package com.x.ssh.aop;

import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.x.ssh.bus.constant.WebSshConstantPool;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.UUID;

/**
 * @ProjectName: webSsh
 * @Package: com.x.ssh.aop
 * @ClassName: WebSocketInterceptor
 * @Author: Yao
 * @Description: Interceptor
 * @CreateDate: 2020/12/29 14:05
 * @Version:
 */
public class WebSocketInterceptor implements HandshakeInterceptor {

    /**
     * handler 处理前调用
     * @param serverHttpRequest request
     * @param serverHttpResponse response
     * @param webSocketHandler handler
     * @param map map
     * @return boolean
     * @throws Exception
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        if (serverHttpRequest instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest request = (ServletServerHttpRequest) serverHttpRequest;
            //生成一个UUID，这里由于是独立项目，没有用户模块，可用随机的UUID
            //若要集成到项目中，需要将其改为自己识别的用户标识
            String uuid = UUID.randomUUID().toString().replace("-", "");
            //将uuid放到webSocketSession中
            map.put(WebSshConstantPool.USER_UUID_KEY, uuid);
            return true;
        }
        return false;
    }

    /**
     *
     * @param serverHttpRequest request
     * @param serverHttpResponse response
     * @param webSocketHandler handler
     * @param e e
     */
    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }
}
