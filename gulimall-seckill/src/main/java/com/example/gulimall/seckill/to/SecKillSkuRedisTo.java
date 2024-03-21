package com.example.gulimall.seckill.to;

import com.example.gulimall.seckill.vo.SkuInfoVo;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 给Redis中缓存秒杀商品信息的to
 */
@Data
@ToString
public class SecKillSkuRedisTo {
    /**
     * id
     */
    private Long id;
    /**
     * 活动id
     */
    private Long promotionId;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀总量
     */
    private BigDecimal seckillCount;
    /**
     * 每人限购数量
     */
    private BigDecimal seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;

    // sku的详细信息
    private SkuInfoVo skuInfo;

    // 当前商品秒杀的开始时间
    private Long startTime;

    // 当前商品秒杀的结束时间
    private Long endTime;

    // 当前sku的秒杀随机码
    private String randomCode;
}
