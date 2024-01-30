package com.example.gulimall.ssoclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @author taoao
 */
@Controller
public class HelloController {

    @Value("${sso.server.url}")
    String ssoServerUrl;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 无需登录就可访问
     *
     * @return
     */
    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello";
    }

    /**
     * 登录才可访问
     *
     * @return
     */
    @GetMapping("/boss")
    public String employees(Model model, HttpSession session, String uuid) {
        if (!StringUtils.isEmpty(uuid)) {

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> forEntity = restTemplate.getForEntity("http://ssoserver.com:8080/userinfo?token=" + uuid, String.class);
            String body = forEntity.getBody();

            session.setAttribute("loginUser", body);
        }

        Object user = session.getAttribute("loginUser");
        if (user == null) {
            // 没登录,跳转到登录服务器登录

            return "redirect:" + ssoServerUrl + "?redirect_url=http://client2.com:8082/boss";
        } else {

            List<String> emps = new ArrayList<>();
            emps.add("张三");
            emps.add("李四");
            emps.add("王五");

            model.addAttribute("emps", emps);

            return "list";
        }
    }
}
