package com.example.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author taoao
 */
@Data
public class FareVo {
    private MemberAddressVo address;
    private BigDecimal fare;
}
