package com.x.ssh.entity;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import org.springframework.web.socket.WebSocketSession;

/**
 * @ProjectName: webSsh
 * @Package: com.x.ssh.entity
 * @ClassName: SshConnectInfo
 * @Author: Yao
 * @Description:
 * @CreateDate: 2020/12/30 9:27
 * @Version:
 */
public class SSHConnectInfo {

    private JSch jSch;

    private WebSocketSession webSocketSession;

    private Channel channel;

    public SSHConnectInfo() {
    }

    public SSHConnectInfo(JSch jSch, WebSocketSession webSocketSession, Channel channel) {
        this.jSch = jSch;
        this.webSocketSession = webSocketSession;
        this.channel = channel;
    }

    public JSch getjSch() {
        return jSch;
    }

    public void setjSch(JSch jSch) {
        this.jSch = jSch;
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public void setWebSocketSession(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
