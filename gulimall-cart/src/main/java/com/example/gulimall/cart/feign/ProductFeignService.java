package com.example.gulimall.cart.feign;

import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author taoao
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {
    /**
     * 根据id查询sku信息
     * @param skuId
     * @return
     */
    @GetMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);

    /**
     * 根据skuId获取属性列表
     * @param skuId
     * @return
     */
    @GetMapping("/product/skusaleattrvalue/stringlist/{skuId}")
    List<String> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);
}
