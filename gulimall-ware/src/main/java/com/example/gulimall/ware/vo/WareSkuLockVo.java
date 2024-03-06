package com.example.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * 锁定库存
 * @author taoao
 */
@Data
public class WareSkuLockVo {
    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 需要锁的所有订单项
     */
    private List<OrderItemVo> locks;


}
