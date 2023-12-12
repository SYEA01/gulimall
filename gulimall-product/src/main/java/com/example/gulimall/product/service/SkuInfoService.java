package com.example.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.gulimall.product.entity.SkuInfoEntity;

import java.util.Map;

/**
 * sku信息
 *
 * @author tpc
 * @email tpc@gmail.com
 * @date 2023-07-31 17:59:38
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存sku的基本信息
     * @param skuInfoEntity
     */
    void saveSkuInfo(SkuInfoEntity skuInfoEntity);
}

