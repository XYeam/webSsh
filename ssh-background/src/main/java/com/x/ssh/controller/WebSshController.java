package com.x.ssh.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ProjectName: webSsh
 * @Package: com.x.ssh.controller
 * @ClassName: WebSshController
 * @Author: Yao
 * @Description:
 * @CreateDate: 2021/1/4 9:50
 * @Version:
 */
@RestController
@RequestMapping("/ssh")
public class WebSshController {

    @RequestMapping("/web")
    public String webSshPage() {
        return "webSsh";
    }
}
