package com.example.gulimall.ssoclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    @GetMapping("/employees")
    public String employees(Model model, HttpSession session) {
        Object user = session.getAttribute("loginUser");
        if (user == null) {
            // 没登录,跳转到登录服务器登录

            return "redirect:" + ssoServerUrl + "?redirect_url=http://client1.com:8081/employees";
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
