package com.example.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.gulimall.ware.entity.WareInfoEntity;
import com.example.gulimall.ware.vo.FareVo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author tpc
 * @email tpc@gmail.com
 * @date 2023-08-02 18:24:16
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取运费信息
     * @param addrId 收货地址id
     * @return 运费
     */
    FareVo getFare(Long addrId);
}

