package com.example.gulimall.ssoserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author taoao
 */
@Controller
public class LoginController {

    @Autowired
    StringRedisTemplate redisTemplate;


    @GetMapping("/login.html")
    public String loginPage(@RequestParam("redirect_url") String redirectUrl, Model model,
                            @CookieValue(value = "sso_token", required = false) String ssoToken) {
        if (!StringUtils.isEmpty(ssoToken)) {
            // 说明之前有人登陆过，浏览器留下了痕迹
            return "redirect:" + redirectUrl + "?uuid=" + ssoToken;
        }

        model.addAttribute("url", redirectUrl);
        return "login";
    }

    @PostMapping("/doLogin")
    public String doLogin(String username, String password, String url, HttpServletResponse response) {
        if (!StringUtils.isEmpty(username) || !StringUtils.isEmpty(password)) {
            // 登录成功跳转，跳回到之前的页面
            String uuid = UUID.randomUUID().toString().replace("-", "");
            redisTemplate.opsForValue().set(uuid, username);

            // 单点登录的关键
            Cookie ssoToken = new Cookie("sso_token", uuid);
            response.addCookie(ssoToken);

            return "redirect:" + url + "?uuid=" + uuid;
        }

        // 登录失败，展示登录页
        return "login";
    }

    @GetMapping("/userinfo")
    @ResponseBody
    public String userInfo(@RequestParam("token") String token) {
        String s = redisTemplate.opsForValue().get(token);
        return s;
    }
}
