package com.example.gulimall.ssoserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

/**
 * @author taoao
 */
@Controller
public class LoginController {

    @Autowired
    StringRedisTemplate redisTemplate;


    @GetMapping("/login.html")
    public String loginPage(@RequestParam("redirect_url") String redirectUrl, Model model) {
        model.addAttribute("url", redirectUrl);
        return "login";
    }

    @PostMapping("/doLogin")
    public String doLogin(String username, String password, String url) {
        if (!StringUtils.isEmpty(username) || !StringUtils.isEmpty(password)) {
            // 登录成功跳转，跳回到之前的页面
            String uuid = UUID.randomUUID().toString().replace("-", "");
            redisTemplate.opsForValue().set(uuid, username);
            return "redirect:" + url + "?uuid=" + uuid;
        }

        // 登录失败，展示登录页
        return "login";
    }
}
