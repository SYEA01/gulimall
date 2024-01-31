package com.example.gulimall.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车中的购物项
 * 需要计算的属性，必须重写它的get方法  ，保证每次获取属性都会进行计算
 * @author taoao
 */
public class CartItem {
    // 商品id
    private Long skuId;

    // 是否被选中
    private Boolean check = true;

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

    // 总价（计算）
    private BigDecimal totalPrice;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getSkuAttr() {
        return skuAttr;
    }

    public void setSkuAttr(List<String> skuAttr) {
        this.skuAttr = skuAttr;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * 计算单个商品的总价
     * @return
     */
    public BigDecimal getTotalPrice() {
        return this.price.multiply(BigDecimal.valueOf(this.count));
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
