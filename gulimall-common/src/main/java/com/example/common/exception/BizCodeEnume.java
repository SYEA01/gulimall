package com.example.common.exception;

/**
 * @author taoao
 * 系统状态码
 * 10:通用
 *      001：参数校验
 *      002：短信验证码频率太高
 * 11：商品
 * 12：订单
 * 13：购物车
 * 14：物流
 * 15：用户
 */
public enum BizCodeEnume {
    UNKNOW_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001, "参数格式校验失败"),
    SMS_CODE_EXCEPTION(10002, "验证码获取频率太高，稍后再试"),
    PRODUCT_UP_EXCEPTION(11000, "商品上架异常"),
    USER_EXIST_EXCEPTION(15001, "用户存在"),
    PHONE_EXIST_EXCEPTION(15002, "手机号存在"),
    LOGINACCT_PASSWORD_INVALID_EXCEPTION(15003, "账号密码错误");


    private int code;
    private String message;

    BizCodeEnume(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
