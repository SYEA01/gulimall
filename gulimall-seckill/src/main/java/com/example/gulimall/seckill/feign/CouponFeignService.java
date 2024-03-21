package com.example.gulimall.seckill.feign;

import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author taoao
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    /**
     * 扫描最近三天需要参与秒杀的活动
     *
     * @return
     */
    @GetMapping("/coupon/seckillsession/latest3DaySession")
    R getLatest3DaySession();
}
