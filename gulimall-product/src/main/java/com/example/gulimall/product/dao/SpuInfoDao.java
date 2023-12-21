package com.example.gulimall.product.dao;

import com.example.gulimall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author tpc
 * @email tpc@gmail.com
 * @date 2023-07-31 17:59:38
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    /**
     * 修改当前spu的状态 改为已上架
     * @param spuId
     * @param code
     */
    void updateSpuStatus(@Param("spuId") Long spuId, @Param("code") int code);
}
