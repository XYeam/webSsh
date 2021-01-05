package com.x.ssh.entity;

/**
 * @ProjectName: webSsh
 * @Package: com.x.ssh.entity
 * @ClassName: WebSshData
 * @Author: Yao
 * @Description:
 * @CreateDate: 2020/12/30 9:27
 * @Version:
 */
public class WebSshData {

    private String operate;

    private String host;

    private Integer port;

    private String userName;

    private String password;

    private String command;

    public WebSshData() {
    }

    public WebSshData(String operate, String host, Integer port, String userName, String password, String command) {
        this.operate = operate;
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.password = password;
        this.command = command;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
