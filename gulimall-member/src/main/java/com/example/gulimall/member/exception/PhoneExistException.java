package com.example.gulimall.member.exception;

/**
 * 手机号已经存在异常
 * 【  继承 RuntimeException  是为了让异常可以抛出去 】
 *
 * @author taoao
 */
public class PhoneExistException extends RuntimeException {

    public PhoneExistException() {
        super("手机号存在");
    }

}
