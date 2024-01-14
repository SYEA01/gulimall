package com.example.gulimall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.gulimall.product.entity.AttrGroupEntity;
import com.example.gulimall.product.vo.SpuItemAttrGroupVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author tpc
 * @email tpc@gmail.com
 * @date 2023-07-31 17:59:38
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}
