package com.example.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author taoao
 */
@Data
public class SkuReductionTo {

    private Long skuId;

    private int fullCount;
    /**
     * 折扣
     */
    private BigDecimal discount;
    private int countStatus;
    /**
     * 满减价格
     */
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;

    /**
     * 会员价格
     */
    private List<MemberPrice> memberPrice;
}
