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
public class Skus {

    private List<Attr> attr;
    private String skuName;
    /**
     * 价格
     */
    private BigDecimal price;
    private String skuTitle;
    private String skuSubtitle;
    private List<Images> images;
    private List<String> descar;
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
    private List<MemberPrice> memberPrice;

}