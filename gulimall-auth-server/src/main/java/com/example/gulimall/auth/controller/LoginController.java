package com.example.gulimall.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author taoao
 */
@Controller
public class LoginController {

    @GetMapping("/login.html")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/reg.html")
    public String regPage() {
        return "reg";
    }

}
