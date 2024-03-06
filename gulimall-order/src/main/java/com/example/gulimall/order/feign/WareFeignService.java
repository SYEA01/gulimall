package com.example.gulimall.order.feign;

import com.example.common.utils.R;
import com.example.gulimall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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

    /**
     * 获取运费信息
     *
     * @return
     */
    @GetMapping("/ware/wareinfo/fare")
    R getFare(@RequestParam("addrId") Long addrId);

    /**
     * 锁库存 （订单）
     *
     * @return
     */
    @PostMapping("/ware/waresku/lock/order")
    R orderLockStock(@RequestBody WareSkuLockVo vo);
}
