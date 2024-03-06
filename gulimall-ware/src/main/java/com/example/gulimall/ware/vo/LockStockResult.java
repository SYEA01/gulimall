package com.example.gulimall.ware.vo;

import lombok.Data;

/**
 * 锁库存 结果
 *
 * @author taoao
 */
@Data
public class LockStockResult {
    /**
     * 商品id
     */
    private Long skuId;

    /**
     * 锁定了几件商品
     */
    private Integer num;

    /**
     * 是否锁定成功
     */
    private Boolean locker;
}
