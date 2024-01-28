package com.example.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.common.constant.AuthServerConstant;
import com.example.common.utils.HttpUtils;
import com.example.common.utils.R;
import com.example.gulimall.auth.feign.MemberFeignService;
import com.example.common.vo.MemberRespVo;
import com.example.gulimall.auth.vo.SocialGiteeUser;
import com.example.gulimall.auth.vo.SocialUser;
import com.example.gulimall.auth.vo.SocialWeiboUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理社交登录请求
 *
 * @author taoao
 */
@Controller
@Slf4j
public class OAuth2Controller {

    @Autowired
    MemberFeignService memberFeignService;

    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session) throws Exception {
        // 1、根据code换取access_token
        Map<String, String> headers = new HashMap<>();
        Map<String, String> querys = new HashMap<>();

        Map<String, String> bodys = new HashMap<>();
        bodys.put("client_id", "2006881721");
        bodys.put("client_secret", "9ea459c69b13bde06c5932a93f7a7bd6");
        bodys.put("grant_type", "authorization_code");
        bodys.put("redirect_uri", "http://auth.gulimall.com/oauth2.0/weibo/success");
        bodys.put("code", code);
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", headers, querys, bodys);

        // 2、处理
        if (response.getStatusLine().getStatusCode() == 200) {
            // 获取到了access_token
            String json = EntityUtils.toString(response.getEntity());
            SocialWeiboUser socialWeiboUser = JSON.parseObject(json, SocialWeiboUser.class);
            SocialUser socialUser = new SocialUser();
            BeanUtils.copyProperties(socialWeiboUser, socialUser);
            socialUser.setIdentification("weibo");

            // 知道当前是哪个社交用户
            // 1）、当前用户如果是第一次进网站，就自动注册进来 （为当前社交用户生成会员信息账号，以后这个社交用户就对应指定的会员）
            // 判断这个社交用户 登录 或者 注册
            R r = memberFeignService.oauthLogin(socialUser);
            if (r.getCode() == 0) {
                MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {
                });
                log.info("登录成功，用户：{}", data.toString());
                // 1、第一次使用session；命令浏览器保存JSESSIONID的 cookie；
                // 以后浏览器访问哪个网站，就会带上这个网站的cookie
                // 子域之间：gulimall.com(父域名) 下面有：auth.gulimall.com、order.gulimall.com。。。。
                // 在设置JSESSIONID的时候，即使是子域，它的作用域要作用到整个父域 直接使用 【 指定域名为父域名 】
                // TODO 1、默认发的令牌：  session=dasdnasjfbahs。  作用域是当前域 （解决子域session共享问题）
                // TODO 2、使用JSON的序列化方式来序列化对象数据到redis中
                session.setAttribute(AuthServerConstant.LOGIN_USER, data);

                // 2、登录成功就跳回首页
                return "redirect:http://gulimall.com";

            } else {
                return "redirect:http://auth.gulimall.com/login.html";
            }
        } else {
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }


    @GetMapping("/oauth2.0/gitee/success")
    public String gitee(@RequestParam("code") String code, HttpSession session) throws Exception {
        // 1、根据code换取access_token
        Map<String, String> headers = new HashMap<>();
        Map<String, String> querys = new HashMap<>();

        Map<String, String> bodys = new HashMap<>();
        bodys.put("client_id", "a3b51e106b62c82a122d8861711a252fbcd0cb79318dfa2a7c36658afeba4a29");
        bodys.put("client_secret", "9db9f68d7980047a9ef7be955a272d409529efd57cfb9e86d4c8f695dd0af213");
        bodys.put("grant_type", "authorization_code");
        bodys.put("redirect_uri", "http://auth.gulimall.com/oauth2.0/gitee/success");
        bodys.put("code", code);
        HttpResponse response = HttpUtils.doPost("https://gitee.com", "/oauth/token", "post", headers, querys, bodys);

        // 2、处理
        if (response.getStatusLine().getStatusCode() == 200) {
            // 获取到了access_token
            String json = EntityUtils.toString(response.getEntity());
            SocialGiteeUser socialGiteeUser = JSON.parseObject(json, SocialGiteeUser.class);
            String accessToken = socialGiteeUser.getAccessToken();
            SocialUser socialUser = new SocialUser();
            BeanUtils.copyProperties(socialGiteeUser, socialUser);
            socialUser.setIdentification("gitee");
            // 查询gitee的用户id
            HashMap<String, String> querys1 = new HashMap<>();
            querys1.put("access_token", accessToken);
            HttpResponse response1 = HttpUtils.doGet("https://gitee.com", "/api/v5/user", "get", new HashMap<String, String>(), querys1);
            String json1 = EntityUtils.toString(response1.getEntity());
            JSONObject jsonObject = JSON.parseObject(json1);
            String uid = jsonObject.getString("id");
            socialUser.setUid(uid);


            // 知道当前是哪个社交用户
            // 1）、当前用户如果是第一次进网站，就自动注册进来 （为当前社交用户生成会员信息账号，以后这个社交用户就对应指定的会员）
            R r = memberFeignService.oauthLogin(socialUser);
            if (r.getCode() == 0) {
                MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {
                });
                log.info("登录成功，用户：{}", data);
                session.setAttribute(AuthServerConstant.LOGIN_USER, data);
                // 2、登录成功就跳回首页
                return "redirect:http://gulimall.com";
            } else {
                return "redirect:http://auth.gulimall.com/login.html";

            }
        } else {
            return "redirect:http://auth.gulimall.com/login.html";
        }


    }
}
