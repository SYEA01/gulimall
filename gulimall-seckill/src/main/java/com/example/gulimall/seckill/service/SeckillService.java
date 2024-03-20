package com.example.gulimall.seckill.service;

/**
 * @author taoao
 */
public interface SeckillService {
    /**
     * 上架最近3天需要秒杀的商品
     */
    void uploadSeckillSkuLatest3Days();
}
