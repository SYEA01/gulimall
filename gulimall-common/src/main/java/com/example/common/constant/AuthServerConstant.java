package com.example.common.constant;

/**
 * @author taoao
 * 认证中心的常量
 */
public class AuthServerConstant {

    // 往redis中存验证码的key的前缀
    public static final String SMS_CODE_CACHE_PREFIX = "sms:code:";

    // 登录成功之后，给session中放的用户信息的key
    public static final String LOGIN_USER = "loginUser";
}
