package com.example.gulimall.ssoserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author taoao
 */
@Controller
public class LoginController {

    @GetMapping("/login.html")
    public String loginPage(@RequestParam("redirect_url") String redirectUrl) {
        return "login";
    }

    @PostMapping("/doLogin")
    public String doLogin() {

        // 登录成功跳转，跳回到之前的页面
        return "";
    }


}
