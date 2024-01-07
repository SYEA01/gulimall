package com.example.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装页面所有可能传递过来的查询条件
 *
 * @author taoao
 */
@Data
public class SearchParam {

    private String keyword;  // 页面传递过来的全文匹配关键字
    private Long catalog3Id;  // 三级分类id

    /**
     * sort = saleCount_asc/desc  // 销量排序
     * sort = skuPrice_asc/desc  // 价格排序
     * sort = hotScore_asc/desc  // 热度评分排序
     */
    private String sort;  // 排序条件

    /**
     * 过滤条件
     * hasStock  是否有货  0/1
     * skuPrice  价格区间  1_500 / _500 / 500_
     * brandId  品牌id  brandId=1&brandId=2&brandId=3
     * attrs  属性  attrs=1_其他:安卓&attrs=2_5寸:6寸
     */
    private Integer hasStock;  // 是否只显示有货
    private String skuPrice;  // 价格区间查询
    private List<Long> brandId;  // 按照品牌id进行筛选（可以多选）
    private List<String> attrs;  // 按照属性筛选


    private Integer pageNum = 1;  // 页码
}
