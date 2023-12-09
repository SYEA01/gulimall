package com.example.gulimall.product.vo;

import lombok.Data;

@Data
public class AttrRespVo extends AttrVo{
    /**
     * 所属分类名字
     */
    private String cateLogName;

    /**
     * 所属分组名字
     */
    private String groupName;


    /**
     * 分类
     */
    private Long[] cateLogPath;

}
