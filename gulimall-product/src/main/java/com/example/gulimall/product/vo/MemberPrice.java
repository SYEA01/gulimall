/**
  * Copyright 2023 bejson.com 
  */
package com.example.gulimall.product.vo;

/**
 * Auto-generated: 2023-12-12 0:20:18
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class MemberPrice {

    /**
     * 会员等级的id
     */
    private int id;
    /**
     * 会员等级的名字
     */
    private String name;
    /**
     * 会员享受的价格
     */
    private int price;
    public void setId(int id) {
         this.id = id;
     }
     public int getId() {
         return id;
     }

    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }

    public void setPrice(int price) {
         this.price = price;
     }
     public int getPrice() {
         return price;
     }

}