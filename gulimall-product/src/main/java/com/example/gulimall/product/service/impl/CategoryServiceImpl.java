package com.example.gulimall.product.service.impl;

import com.example.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.gulimall.product.dao.CategoryDao;
import com.example.gulimall.product.entity.CategoryEntity;
import com.example.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

//    @Autowired
//    CategoryDao categoryDao;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 1、查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        // 2、组装成父子的树形结构
        // 2.1）、找到所有的1级分类
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity -> categoryEntity.getParentCid() == 0).map(menu -> {
            // 2.2）、 递归设置子菜单
            menu.setChildren(getChildrens(menu, entities));
            return menu;
            // 2.3）、给所有的一级菜单排序
        }).sorted((preMenu, nextMenu) -> {  // (前一个菜单,后一个菜单)
            return (preMenu.getSort() == null ? 0 : preMenu.getSort()) - (nextMenu.getSort() == null ? 0 : nextMenu.getSort());
        }).collect(Collectors.toList());


        return level1Menus;
    }


    /**
     * 递归查询所有菜单的子菜单
     *
     * @param root 当前菜单
     * @param all  所有菜单
     * @return
     */
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid().equals(root.getCatId()))
                .map(categoryEntity -> {
                    // 1、找到子菜单
                    categoryEntity.setChildren(getChildrens(categoryEntity, all));  // 为当前菜单的子菜单设置子菜单
                    return categoryEntity;
                }).sorted((preMenu, nextMenu) -> {
                    // 2、菜单的排序
                    return (preMenu.getSort() == null ? 0 : preMenu.getSort()) - (nextMenu.getSort() == null ? 0 : nextMenu.getSort());
                }).collect(Collectors.toList());
        return children;
    }

    @Override
    public void removeMenuByIds(List<Long> list) {
        //TODO   1、检查当前删除的菜单，是否被别的地方引用

        // 2、逻辑删除
        baseMapper.deleteBatchIds(list);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);
        return (Long[]) parentPath.toArray(new Long[parentPath.size()]);
    }

    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
    }


    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        // 1、收集当前节点id
        paths.add(catelogId);
        CategoryEntity categoryEntity = this.getById(catelogId);

        if (categoryEntity.getParentCid() != 0) {
            findParentPath(categoryEntity.getParentCid(), paths);
        }
        return paths;
    }

}