package com.example.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 封装订单提交的数据
 * @author taoao
 */
@Data
public class OrderSubmitVo {

    /**
     * 收货地址id
     */
    private Long addrId;

    /**
     * 支付方式
     */
    private Integer payType;

    /**
     * 无需提交需要购买的商品，去购物车再获取一遍
     */

    /**
     * 用户相关信息去session中取
     */

    /**
     * 应付价格 验价
     */
    private BigDecimal payPrice;

    /**
     * 订单备注
     */
    private String note;

    /**
     * 防重令牌
     */
    private String orderToken;


}
