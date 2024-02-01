package com.example.gulimall.cart.controller;

import com.example.common.constant.AuthServerConstant;
import com.example.gulimall.cart.interceptor.CartInterceptor;
import com.example.gulimall.cart.to.UserInfoTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

/**
 * @author taoao
 */
@Controller
public class CartController {

    /**
     * 去购物车页面
     * 浏览器有一个cookie：user-key：标识用户身份，一个月后过期
     * 如果第一次使用京东的购物车功能，都会给一个临时的user-key
     * 浏览器以后保存，每次访问都会带上这个cookie；
     * <p>
     * 如果登录了，session中有
     * 如果没登录，就按照cookie里面带的user-key来做
     * 第一次；如果没有临时用户，帮忙创建一个临时用户
     *
     * @return
     */
    @GetMapping("/cart.html")
    public String cartListPage() {

        // 获取拦截器中的ThreadLocal中的数据
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        System.out.println("userInfoTo = " + userInfoTo);

        return "cartList";
    }

    /**
     * 添加商品到购物车
     * @return
     */
    @GetMapping("/addToCart")
    public String addToCart() {

        return "success";
    }

}
