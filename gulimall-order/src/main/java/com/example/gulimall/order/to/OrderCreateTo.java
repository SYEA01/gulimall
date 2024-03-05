package com.example.gulimall.order.to;

import com.example.gulimall.order.entity.OrderEntity;
import com.example.gulimall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author taoao
 */
@Data
public class OrderCreateTo {
    /**
     * 创建好的订单
     */
    private OrderEntity order;

    /**
     * 订单项
     */
    private List<OrderItemEntity> orderItems;

    /**
     * 订单计算的应付价格
     */
    private BigDecimal payPrice;

    /**
     * 运费
     */
    private BigDecimal fare;

}
