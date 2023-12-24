package com.example.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author taoao
 * 二级分类Vo
 */
@Data
@AllArgsConstructor  // 有参构造器
@NoArgsConstructor  // 无参构造器
public class Catelog2Vo {
    private String catalog1Id;  // 一级父分类id
    private List<Catelog3Vo> catalog3List;  // 三级子分类
    private String id;  // 当前2级分类的id
    private String name;  // 当前2级分类的名字

    /**
     * 三级分类Vo
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catelog3Vo{
        private String catalog2Id;  // 父分类，也就是2级分类id
        private String id;  // 当前3级分类的id
        private String name;  // 当前3级分类的名字
    }
}
