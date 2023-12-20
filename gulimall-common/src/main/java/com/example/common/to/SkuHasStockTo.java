package com.example.common.to;

import lombok.Data;

/**
 * @author taoao
 */
@Data
public class SkuHasStockTo {
    private Long skuId;
    /**
     * 当前sku是否有库存
     */
    private Boolean hasStock;
}
