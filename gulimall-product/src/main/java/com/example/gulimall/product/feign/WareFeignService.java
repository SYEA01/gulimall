package com.example.gulimall.product.feign;

import com.example.common.to.SkuHasStockTo;
import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author taoao
 */
@FeignClient("gulimall-ware")
public interface WareFeignService {
    /**
     * 查询sku是否有库存
     */
    @PostMapping("/ware/waresku/hasstock")
    R getSkusHasStock(@RequestBody List<Long> skuIds);
}
