package com.example.gulimall.member.exception;

/**
 * 用户名已经存在异常
 * 【  继承 RuntimeException  是为了让异常可以抛出去 】
 *
 * @author taoao
 */
public class UsernameExistException extends RuntimeException {

    public UsernameExistException() {
        super("用户名存在");
    }
}
