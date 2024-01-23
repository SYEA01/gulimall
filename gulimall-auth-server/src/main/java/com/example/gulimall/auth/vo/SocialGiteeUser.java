package com.example.gulimall.auth.vo;

import lombok.Data;

/**
 * Gitee社交登录实体
 * @author taoao
 */
@Data
public class SocialGiteeUser {
    /**
     * accessToken
     */
    private String accessToken;
    /**
     * 令牌类型
     */
    private String tokenType;
    /**
     * 过期时间
     */
    private long expiresIn;
    /**
     * 刷新令牌
     */
    private String refreshToken;
    /**
     * 范围
     */
    private String scope;
    /**
     * 创建于
     */
    private long createdAt;
}
