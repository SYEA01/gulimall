package com.example.gulimall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author taoao
 */
@Data
public class UserRegistVo {

    @NotEmpty(message = "用户名必须提交")
    @Length(min = 6, max = 18, message = "用户名必须是6-18位的字符")
    private String userName;
    @NotEmpty(message = "密码必须填写")
    @Length(min = 6, max = 18, message = "密码必须是6-18位的字符")
    private String password;
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$", message = "手机号格式不正确")
    @NotEmpty(message = "手机号必须填写")
    private String phone;
    @NotEmpty(message = "验证码必须填写")
    private String code;
}
