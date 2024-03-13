package com.example.gulimall.ware.feign;

import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author taoao
 */
@FeignClient("gulimall-order")
public interface OrderFeignService {
    /**
     * 根据订单id返回订单状态
     */
    @GetMapping("/order/order/status/{orderSn}")
    R getOrderStatus(@PathVariable String orderSn);
}
