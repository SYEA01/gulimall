package com.example.gulimall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * sku的销售属性
 *
 * @author taoao
 */
@Data
@ToString
public class SkuItemSaleAttrVo {
    private Long attrId;
    private String attrName;
    private String attrValues;
}
