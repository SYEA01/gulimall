package com.example.gulimall.order.vo;

import lombok.Data;

/**
 * @author taoao
 */
@Data
public class SkuStockVo {
    private Long skuId;
    /**
     * 当前sku是否有库存
     */
    private Boolean hasStock;
}
