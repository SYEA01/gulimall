package com.example.gulimall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车所有选中的购物项
 * @author taoao
 */
@Data
public class OrderItemVo {

    // 商品id
    private Long skuId;

    // 商品标题
    private String title;

    // 商品图片
    private String image;

    // 套餐信息
    private List<String> skuAttr;

    // 商品价格
    private BigDecimal price;

    // 商品数量
    private Integer count;

    //
    private BigDecimal totalPrice;


    // 商品重量
    private BigDecimal weight;
}
