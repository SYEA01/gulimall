package com.example.gulimall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.common.utils.R;
import com.example.gulimall.seckill.feign.CouponFeignService;
import com.example.gulimall.seckill.service.SeckillService;
import com.example.gulimall.seckill.to.SecKillSkuRedisTo;
import com.example.gulimall.seckill.vo.SeckillSessionsWithSkus;
import com.example.gulimall.seckill.vo.SeckillSkuVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author taoao
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    // 缓存 活动的key的前缀
    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";

    // 缓存 秒杀商品的key的前缀
    private final String SKUKILL_CACHE_PREFIX = "seckill:skus:";

    @Override
    public void uploadSeckillSkuLatest3Days() {
        // 1、去扫描最近三天需要参与秒杀的活动
        R session = couponFeignService.getLatest3DaySession();
        if (session.getCode() == 0) {
            //远程调用成功，上架商品
            List<SeckillSessionsWithSkus> sessionData = session.getData(new TypeReference<List<SeckillSessionsWithSkus>>() {
            });
            // 缓存到redis
            // 1、保存秒杀活动信息、
            saveSessionInfos(sessionData);
            // 2、以及保存秒杀商品信息
            saveSessionSkuInfos(sessionData);

        }

    }

    /**
     * 保存秒杀活动信息
     *
     * @param sessions
     */
    private void saveSessionInfos(List<SeckillSessionsWithSkus> sessions) {
        sessions.forEach(session -> {
            // 开始时间和结束时间的时间戳
            Long startTime = session.getStartTime().getTime();
            Long endTime = session.getEndTime().getTime();
            // redis 中的key
            String key = SESSIONS_CACHE_PREFIX + startTime + "_" + endTime;
            // 这个商品的所有商品的skuId
            List<String> skuIds = session.getRelationSkus().stream().map(item -> item.getSkuId().toString()).collect(Collectors.toList());
            // 往redis中存放秒杀活动信息
            redisTemplate.opsForList().leftPushAll(key, skuIds);
        });
    }

    /**
     * 保存秒杀商品信息
     *
     * @param sessions
     */
    private void saveSessionSkuInfos(List<SeckillSessionsWithSkus> sessions) {
        sessions.forEach(session -> {
            // 准备hash操作  key->value 结构的
            BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
            session.getRelationSkus().forEach(sku -> {
                // 缓存秒杀商品信息
                SecKillSkuRedisTo secKillSkuRedisTo = new SecKillSkuRedisTo();
                // 1、sku的基本数据

                // 2、sku的秒杀信息
                BeanUtils.copyProperties(sku, secKillSkuRedisTo);
                // 3、sku随机码

                String s = JSON.toJSONString(secKillSkuRedisTo);
                ops.put(sku.getId(), s);
            });
        });
    }

}
