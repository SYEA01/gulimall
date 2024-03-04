package com.example.gulimall.order.vo;

import com.example.gulimall.order.entity.OrderEntity;
import lombok.Data;

/**
 * @author taoao
 */
@Data
public class SubmitOrderResponseVo {
    /**
     * 订单信息
     */
    private OrderEntity order;

    /**
     * 错误状态码  0- 成功
     */
    private Integer code;
}
