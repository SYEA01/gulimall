package com.example.gulimall.seckill.feign;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author taoao
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

}
