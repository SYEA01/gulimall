package com.example.gulimall.auth.vo;

import lombok.Data;

/**
 * 社交登录传参
 * @author taoao
 */
@Data
public class SocialUser {

    /**
     * accessToken
     */
    private String accessToken;
    /**
     * 过期时间
     */
    private long expiresIn;
    /**
     * 用户的唯一标识
     */
    private String uid;

    /**
     * 标识：weibo还是gitee
     */
    private String identification;
}
