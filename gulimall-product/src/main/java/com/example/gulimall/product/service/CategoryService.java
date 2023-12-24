package com.example.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.gulimall.product.entity.CategoryEntity;
import com.example.gulimall.product.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author tpc
 * @email tpc@gmail.com
 * @date 2023-07-31 17:59:38
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查出所有分类以及子分类，以树形结构组装起来
     */
    List<CategoryEntity> listWithTree();

    /**
     * 删除
     */
    void removeMenuByIds(List<Long> list);

    /**
     * 找到catelogId 的完整路径
     * @param catelogId
     * @return
     */
    Long[] findCatelogPath(Long catelogId);

    /**
     * 级联更新所有关联的数据
     * @param category
     */
    void updateCascade(CategoryEntity category);

    /**
     * 查出所有的 1级分类
     * @return
     */
    List<CategoryEntity> getLevel1Categorys();

    /**
     * 返回二级分类三级分类的json数据
     * @return
     */
    Map<String, List<Catelog2Vo>> getCatalogJson();
}

