package com.example.gulimall.ware.vo;

import lombok.Data;

/**
 * @author taoao
 */
@Data
public class PurchaseItemDoneVo {

    private Long itemId;

    private Integer status;

    private String reason;
}
