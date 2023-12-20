package com.example.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.gulimall.product.entity.SpuInfoDescEntity;
import com.example.gulimall.product.entity.SpuInfoEntity;
import com.example.gulimall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author tpc
 * @email tpc@gmail.com
 * @date 2023-07-31 17:59:38
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    /**
     * 保存Spu基本信息
     * @param spuInfoEntity
     */
    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

    /**
     * spu检索
     * @param params
     * @return
     */
    PageUtils queryPageByCondition(Map<String, Object> params);

    /**
     * 商品上架
     * @param spuId
     */
    void up(Long spuId);
}

