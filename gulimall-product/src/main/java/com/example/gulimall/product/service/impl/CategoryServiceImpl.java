package com.example.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;
import com.example.gulimall.product.dao.CategoryDao;
import com.example.gulimall.product.entity.CategoryEntity;
import com.example.gulimall.product.service.CategoryBrandRelationService;
import com.example.gulimall.product.service.CategoryService;
import com.example.gulimall.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

//    private Map<String, Object> cache = new HashMap<>();

//    @Autowired
//    CategoryDao categoryDao;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate redisTemplate;

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
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
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

    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        long start = System.currentTimeMillis();
        List<CategoryEntity> entities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        long end = System.currentTimeMillis();
        System.out.println("消耗时间： " + (end - start));
        return entities;
    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        // 给缓存中放JSON字符串，拿出的JSON字符串还要逆转为能用的对象类型； 【 序列化与反序列化 】

        // 1、加入缓存逻辑，缓存中存的数据是JSON字符串
        // JSON的好处：跨语言跨平台兼容
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catalogJSON)) {
            // 2、缓存中没有，查询数据库
            Map<String, List<Catelog2Vo>> catalogJsonFromDb = getCatalogJsonFromDb();
            // 3、将查到的数据再放入缓存,将对象转为JSON放到缓存中
            String jsonString = JSON.toJSONString(catalogJsonFromDb);
            redisTemplate.opsForValue().set("catalogJSON", jsonString);
            return catalogJsonFromDb;
        }

        // 把从缓存中查到的字符串转为指定的对象
        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });

        return result;
    }

    // 从数据库查询并封装整个分类数据
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDb() {

//        // 1、如果缓存中有，就用缓存的
//        Map<String, List<Catelog2Vo>> catalogJson = (Map<String, List<Catelog2Vo>>) cache.get("catalogJson");
//        if (catalogJson == null) {
//            // 调用业务，返回数据又放入缓存
//            cache.put("catalogJson", map);
//        }
//        return catalogJson;

        /**
         * 第一种优化：将数据库的多次查询变为一次
         */
        List<CategoryEntity> allList = baseMapper.selectList(null);

        // 1、查出所有1级分类
        List<CategoryEntity> level1Categorys = getParentCid(allList, 0L);

        // 2、封装数据
        Map<String, List<Catelog2Vo>> map = level1Categorys.stream().collect(Collectors.toMap(key -> key.getCatId().toString(), value -> {
            // 1、每一个的一级分类，查到这个一级分类的所有二级分类
            List<CategoryEntity> category2List = getParentCid(allList, value.getParentCid());
            // 2、封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (category2List != null) {
                catelog2Vos = category2List.stream().map(c2 -> {
                    // 1、找当前分类的三级分类封装成Vo
                    List<CategoryEntity> category3List = getParentCid(allList, c2.getCatId());
                    List<Catelog2Vo.Catelog3Vo> catelog3Vos = null;
                    if (category3List != null) {
                        catelog3Vos = category3List.stream().map(c3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(c2.getCatId().toString(), c3.getCatId().toString(), c3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                    }
                    Catelog2Vo catelog2Vo = new Catelog2Vo(value.getCatId().toString(), catelog3Vos, c2.getCatId().toString(), c2.getName());
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        return map;
    }

    private List<CategoryEntity> getParentCid(List<CategoryEntity> allList, Long parentCid) {
//        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", value.getCatId()));
        List<CategoryEntity> collect = allList.stream().filter(item -> item.getParentCid().equals(parentCid)).collect(Collectors.toList());
        return collect;
    }

}