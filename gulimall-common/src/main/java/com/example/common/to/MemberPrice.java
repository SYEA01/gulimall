/**
  * Copyright 2023 bejson.com 
  */
package com.example.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Auto-generated: 2023-12-12 0:20:18
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class MemberPrice {

    /**
     * 会员等级的id
     */
    private Long id;
    /**
     * 会员等级的名字
     */
    private String name;
    /**
     * 会员享受的价格
     */
    private BigDecimal price;

}