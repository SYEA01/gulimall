package com.example.gulimall.ware.feign;

import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author taoao
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {
    /**
     * 有两种写法
     *  1、不过网关，直接让后台指定服务处理
     *     @FeignClient("gulimall-product")   /product/skuinfo/info/{skuId}
     *  2、让所有请求过网关
     *     @FeignClient("gulimall-gateway")  /api/product/skuinfo/info/{skuId}
     * @return
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);
}
