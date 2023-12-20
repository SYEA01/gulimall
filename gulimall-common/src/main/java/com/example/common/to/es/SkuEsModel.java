package com.example.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author taoao
 */
@Data
public class SkuEsModel {
    private Long skuId;

    private Long spuId;

    private String skuTitle;

    /**
     * sku价格
     */
    private BigDecimal skuPrice;

    /**
     * 默认图片
     */
    private String skuImg;

    /**
     * 销量
     */
    private Long saleCount;

    /**
     * 是否拥有库存
     */
    private Boolean hasStock;

    /**
     * 热度评分
     */
    private Long hotScore;

    /**
     * 品牌id
     */
    private Long brandId;

    /**
     * 分类id
     */
    private Long catalogId;

    /**
     * 品牌名字
     */
    private String brandName;

    /**
     * 品牌的图片
     */
    private String brandImg;

    /**
     * 分类的名字
     */
    private String catalogName;

    private List<Attrs> attrs;

    /**
     * sku的规格属性
     */
    @Data
    public static class Attrs {

        private Long attrId;

        private String attrName;

        private String attrValue;
    }
}
