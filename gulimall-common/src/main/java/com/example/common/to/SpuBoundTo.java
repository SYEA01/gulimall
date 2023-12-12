package com.example.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author taoao
 */
@Data
public class SpuBoundTo {
    /**
     * spuId
     */
    private Long spuId;

    /**
     * 购物积分
     */
    private BigDecimal buyBounds;
    /**
     * 成长积分
     */
    private BigDecimal growBounds;


}
