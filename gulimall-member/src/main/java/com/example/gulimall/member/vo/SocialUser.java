package com.example.gulimall.member.vo;

import lombok.Data;

/**
 * 微博实体
 * @author taoao
 */
@Data
public class SocialUser {
    /**
     * accessToken
     */
    private String accessToken;
    /**
     * accessToken的生命周期
     */
    private String remindIn;
    /**
     * 过期时间
     */
    private long expiresIn;
    /**
     * 用户的唯一标识
     */
    private String uid;
    /**
     * 是真实姓名？
     */
    private String isRealName;

    /**
     * 标识
     */
    private String identification;
}
