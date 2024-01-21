package com.example.gulimall.member.vo;

import lombok.Data;

/**
 * 登录用的vo
 *
 * @author taoao
 */
@Data
public class MemberLoginVo {
    // 登录的账号
    private String loginacct;
    // 密码
    private String password;
}
