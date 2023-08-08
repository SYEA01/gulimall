package com.example.gulimall.ware.dao;

import com.example.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author tpc
 * @email tpc@gmail.com
 * @date 2023-08-02 18:24:16
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
