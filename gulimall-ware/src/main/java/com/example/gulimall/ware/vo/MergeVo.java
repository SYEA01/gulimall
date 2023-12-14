package com.example.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author taoao
 */
@Data
public class MergeVo {
    private Long purchaseId;
    private List<Long> items;
}
