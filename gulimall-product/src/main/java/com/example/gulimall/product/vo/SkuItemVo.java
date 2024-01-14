package com.example.gulimall.product.vo;

import com.example.gulimall.product.entity.SkuImagesEntity;
import com.example.gulimall.product.entity.SkuInfoEntity;
import com.example.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @author taoao
 */
@Data
public class SkuItemVo {
    // 1、获取sku的基本信息  pms_sku_info表
    private SkuInfoEntity info;
    boolean hasStock = true;

    // 2、获取sku的图片信息 pms_sku_images表
    private List<SkuImagesEntity> images;

    // 3、获取sku的销售属性组合
    private List<SkuItemSaleAttrVo> saleAttr;

    // 4、获取spu的介绍  pms_spu_info_desc表
    private SpuInfoDescEntity desc;

    // 5、获取spu的规格参数信息
    private List<SpuItemAttrGroupVo> groupAttrs;
}
