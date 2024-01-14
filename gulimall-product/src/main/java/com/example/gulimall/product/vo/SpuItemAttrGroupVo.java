package com.example.gulimall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * spu的规格参数信息
 * @author taoao
 */
@Data
@ToString
public class SpuItemAttrGroupVo {
    private String groupName;
    private List<Attr> attrs;
}
