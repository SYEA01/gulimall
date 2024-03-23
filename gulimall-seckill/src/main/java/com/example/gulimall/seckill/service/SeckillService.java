package com.example.gulimall.seckill.service;

import com.example.gulimall.seckill.to.SecKillSkuRedisTo;

import java.util.List;

/**
 * @author taoao
 */
public interface SeckillService {
    /**
     * 上架最近3天需要秒杀的商品
     */
    void uploadSeckillSkuLatest3Days();

    /**
     * 返回当前时间可以参与的秒杀商品信息
     * @return
     */
    List<SecKillSkuRedisTo> getCurrentSeckillSkus();

    /**
     * 查询当前sku是否参与秒杀优惠
     * @param skuId
     * @return
     */
    SecKillSkuRedisTo getSkuSeckillInfo(Long skuId);
}
