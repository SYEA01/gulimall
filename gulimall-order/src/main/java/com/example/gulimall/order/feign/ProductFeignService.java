package com.example.gulimall.order.feign;

import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author taoao
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {
    /**
     * 根据skuId查询spu信息
     *
     * @param skuId
     * @return
     */
    @GetMapping("/product/spuinfo/skuId/{skuId}")
    R getSpuInfoBySkuId(@PathVariable Long skuId);
}
