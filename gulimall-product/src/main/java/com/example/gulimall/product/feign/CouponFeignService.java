package com.example.gulimall.product.feign;

import com.example.common.to.SpuBoundTo;
import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author taoao
 */
@FeignClient("gulimall-coupon")  // 调用哪个远程服务
public interface CouponFeignService {

    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);
}
