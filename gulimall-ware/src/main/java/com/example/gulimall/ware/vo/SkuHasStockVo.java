package com.example.gulimall.ware.vo;

import lombok.Data;

/**
 * @author taoao
 */
@Data
public class SkuHasStockVo {
    private Long skuId;
    /**
     * 当前sku是否有库存
     */
    private Boolean hasStock;
}
