package com.example.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.gulimall.product.entity.BrandEntity;
import com.example.gulimall.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author tpc
 * @email tpc@gmail.com
 * @date 2023-07-31 17:59:38
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    void updateBrand(Long brandId, String name);

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateCategory(Long catId, String name);

    /**
     * 查询指定分类的所有品牌信息
     * @param catId
     * @return
     */
    List<BrandEntity> getBrandsByCatId(Long catId);
}

