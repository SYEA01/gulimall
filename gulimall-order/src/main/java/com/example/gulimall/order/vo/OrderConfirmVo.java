package com.example.gulimall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单确认页需要用的数据
 *
 * @author taoao
 */
public class OrderConfirmVo {
    // 收货地址：ums_member_receive_address表
    @Getter
    @Setter
    private List<MemberAddressVo> address;

    // 所有选中的购物项
    @Getter
    @Setter
    private List<OrderItemVo> items;

    // 发票记录。。。

    // 优惠劵信息
    @Getter
    @Setter
    private Integer integration;

    // 订单总额
//    private BigDecimal total;
    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if (items != null) {
            for (OrderItemVo item : items) {
                BigDecimal price = item.getPrice().multiply(BigDecimal.valueOf(item.getCount()));
                sum = sum.add(price);
            }
        }
        return sum;
    }

    // 应付价格
//    private BigDecimal payPrice;
    public BigDecimal getPayPrice() {
        return getTotal();
    }

    // 订单的放重复提交令牌
    @Getter
    @Setter
    private String orderToken;

    public Integer getCount() {
        Integer i = 0;
        if (items != null) {
            for (OrderItemVo item : items) {
                i += item.getCount();
            }
        }
        return i;
    }
}
