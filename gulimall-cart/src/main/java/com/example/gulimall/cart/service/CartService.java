package com.example.gulimall.cart.service;

import com.example.gulimall.cart.vo.CartItem;

import java.util.concurrent.ExecutionException;

/**
 * @author taoao
 */
public interface CartService {
    /**
     * 添加商品到购物车
     * @param skuId
     * @param num
     * @return
     */
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;
}
