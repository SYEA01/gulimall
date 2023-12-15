package com.example.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.gulimall.ware.entity.WareSkuEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * 商品库存
 *
 * @author tpc
 * @email tpc@gmail.com
 * @date 2023-08-02 18:24:16
 */
public interface WareSkuService extends IService<WareSkuEntity> {
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 将成功采购的 进行入库
     * @param skuId
     * @param wareId
     * @param skuNum
     */
    void addStock(Long skuId, Long wareId, Integer skuNum);
}

