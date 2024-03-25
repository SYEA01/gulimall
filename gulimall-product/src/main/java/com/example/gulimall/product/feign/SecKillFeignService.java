package com.example.gulimall.product.feign;

import com.example.common.utils.R;
import com.example.gulimall.product.feign.fallback.SecKillFeignServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * fallback 远程调用失败后的默认回调
 */
@FeignClient(value = "gulimall-seckill", fallback = SecKillFeignServiceFallBack.class)
public interface SecKillFeignService {
    /**
     * 查询当前sku是否参与秒杀优惠
     *
     * @param skuId
     * @return
     */
    @GetMapping("/sku/seckill/{skuId}")
    R getSkuSeckillInfo(@PathVariable Long skuId);
}
