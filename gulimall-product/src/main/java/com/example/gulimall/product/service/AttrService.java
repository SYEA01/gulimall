package com.example.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.gulimall.product.entity.AttrEntity;
import com.example.gulimall.product.vo.AttrGroupRelationVo;
import com.example.gulimall.product.vo.AttrRespVo;
import com.example.gulimall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author tpc
 * @email tpc@gmail.com
 * @date 2023-07-31 17:59:38
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attyType);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    /**
     * 根据分组id找到组内关联的所有基本属性
     * @param attrgroupId
     * @return
     */
    List<AttrEntity> getRelationAttr(Long attrgroupId);

    void deleteRelation(AttrGroupRelationVo[] vos);

    /**
     * 获取当前分组没有关联的所有属性
     * @param params
     * @param attrgroupId
     * @return
     */
    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    /**
     * 在指定的所有属性集合中挑出检索属性
     * @param attrIds
     * @return
     */
    List<Long> selectSearchAttrIds(List<Long> attrIds);
}

