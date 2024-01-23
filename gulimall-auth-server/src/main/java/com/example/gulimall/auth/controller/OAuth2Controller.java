package com.example.gulimall.auth.controller;

import com.example.common.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理社交登录请求
 *
 * @author taoao
 */
@Controller
public class OAuth2Controller {

    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code) throws Exception {
        // 1、根据code换取access_token
        Map<String, String> bodys = new HashMap<>();
        bodys.put("client_id", "2006881721");
        bodys.put("client_secret", "9ea459c69b13bde06c5932a93f7a7bd6");
        bodys.put("grant_type", "authorization_code");
        bodys.put("redirect_uri", "http://gulimall.com/oauth2.0/weibo/success");
        bodys.put("code", code);
        HttpResponse post = HttpUtils.doPost("api.weibo.com", "/oauth2/access_token", "post", null, null, bodys);

        // 2、处理

        // 2、登录成功就跳回首页
        return "redirect:http://gulimall.com";
    }


    @GetMapping("/oauth2.0/gitee/success")
    public String gitee(@RequestParam("code") String code) throws Exception {
        // 1、根据code换取access_token
        Map<String, String> bodys = new HashMap<>();
        bodys.put("client_id", "a3b51e106b62c82a122d8861711a252fbcd0cb79318dfa2a7c36658afeba4a29");
        bodys.put("client_secret", "9db9f68d7980047a9ef7be955a272d409529efd57cfb9e86d4c8f695dd0af213");
        bodys.put("grant_type", "authorization_code");
        bodys.put("redirect_uri", "http://gulimall.com/oauth2.0/gitee/success");
        bodys.put("code", code);
        HttpResponse post = HttpUtils.doPost("gitee.com", "/oauth/token", "post", null, null, bodys);

        // 2、处理

        // 2、登录成功就跳回首页
        return "redirect:http://gulimall.com";
    }
}
