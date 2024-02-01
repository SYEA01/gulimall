package com.example.gulimall.cart.to;

import lombok.Data;
import lombok.ToString;

/**
 * @author taoao
 */
@Data
@ToString
public class UserInfoTo {
    // 如果用户登录了，会有用户id
    private Long userId;

    // 如果没登录，会有一个user-key
    private String userKey;


    private boolean tempUser = false;
}
