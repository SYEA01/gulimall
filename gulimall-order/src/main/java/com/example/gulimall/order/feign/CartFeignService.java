package com.example.gulimall.order.feign;

import com.example.gulimall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author taoao
 */
@FeignClient("gulimall-cart")
public interface CartFeignService {
    /**
     * 远程查询购物车中所有选中的购物项
     * @return
     */
    @GetMapping("/currentUserCartItems")
    List<OrderItemVo> getCurrentUserCartItems();
}
