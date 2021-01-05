function WebSshClient() {

};

WebSshClient.prototype._generateEndpoint = function () {
    if (window.location.protocol == 'https:') {
        var protocol = 'wss://';
    }else {
        var protocol = 'ws://';
    }
    var endpoint = protocol + '127.0.0.1:8080/ssh/web';
    return endpoint;
};

WebSshClient.prototype.connect = function (options) {
    var endpoint = this._generateEndpoint();
    if (window.WebSocket) {
        //如果支持webSocket
        this._connection = new WebSocket(endpoint);
    }else {
        //否则报错
        options.onerror('WebSocket Not Support');
        return;
    }

    this._connection.onopen = function () {
        options.onConnect();
    };

    this._connection.onmessage = function (ev) {
        var data = ev.data.toString();
        options.onData(data);
    };
    this._connection.onclose = function (ev) {
        options.onclose();
    };
};

WebSshClient.prototype.send = function (data) {
    this._connection.send(JSON.stringify(data));
};

WebSshClient.prototype.sendInitData = function (options) {
    //连接参数
    this._connection.send(JSON.stringify(options));
};

WebSshClient.prototype.sendClientData = function (data) {
    //发送指令
    this._connection.send(JSON.stringify({"operate":"command", "command": data}))
};

var client = new WebSshClient();

