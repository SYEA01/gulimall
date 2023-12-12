/**
  * Copyright 2023 bejson.com 
  */
package com.example.gulimall.product.vo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2023-12-12 0:20:18
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class SpuSaveVo {

    private String spuName;
    private String spuDescription;
    private Long catalogId;
    private Long brandId;
    /**
     * 商品重量
     */
    private BigDecimal weight;
    /**
     * 发布状态
     */
    private int publishStatus;
    /**
     * 描述
     */
    private List<String> decript;
    /**
     * 图片
     */
    private List<String> images;
    private Bounds bounds;
    private List<BaseAttrs> baseAttrs;
    private List<Skus> skus;


}