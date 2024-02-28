package com.example.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单确认页需要用的数据
 * @author taoao
 */
@Data
public class OrderConfirmVo {
    // 收货地址：ums_member_receive_address表
    private List<MemberAddressVo> address;

    // 所有选中的购物项
    private List<OrderItemVo> items;

    // 发票记录。。。

    // 优惠劵信息
    private Integer integration;

    // 订单总额
    private BigDecimal total;
    // 应付价格
    private BigDecimal payPrice;
}
