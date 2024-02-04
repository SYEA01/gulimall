package com.example.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.common.utils.R;
import com.example.gulimall.cart.feign.ProductFeignService;
import com.example.gulimall.cart.interceptor.CartInterceptor;
import com.example.gulimall.cart.service.CartService;
import com.example.gulimall.cart.to.UserInfoTo;
import com.example.gulimall.cart.vo.CartItem;
import com.example.gulimall.cart.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author taoao
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    private final String CART_PREFIX = "gulimall:cart:";


    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        // 获取到要操作的购物车
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        String res = (String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(res)) {
            // 购物车无此商品
            // 2、添加新商品到购物车
            CartItem cartItem = new CartItem();

            // 1、远程查询当前要添加的商品信息
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                R r = productFeignService.getSkuInfo(skuId);
                SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });

                cartItem.setCheck(true);
                cartItem.setCount(num);
                cartItem.setImage(skuInfo.getSkuDefaultImg());
                cartItem.setTitle(skuInfo.getSkuTitle());
                cartItem.setSkuId(skuId);
                cartItem.setPrice(skuInfo.getPrice());
            }, executor);

            // 3、远程查询sku的属性组合信息
            CompletableFuture<Void> getSkuSaleAttrValuesTask = CompletableFuture.runAsync(() -> {
                List<String> values = productFeignService.getSkuSaleAttrValues(skuId);
                cartItem.setSkuAttr(values);
            }, executor);

            CompletableFuture.allOf(getSkuInfoTask, getSkuSaleAttrValuesTask).get();

            String jsonString = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(), jsonString);
            return cartItem;
        } else {
            // 购物车有这个商品，修改数量即可
            CartItem cartItem = JSON.parseObject(res, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);
            String jsonString = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(), jsonString);
            return cartItem;
        }
    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String o = (String) cartOps.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(o, CartItem.class);
        return cartItem;
    }

    /**
     * 获取到要操作的购物车
     *
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId() != null) {
            // 登录了
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        } else {
            // 没登录
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }

        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }
}
