package com.example.gulimall.ware.service.impl;

import com.example.common.utils.R;
import com.example.gulimall.ware.exception.NoStockException;
import com.example.gulimall.ware.feign.ProductFeignService;
import com.example.gulimall.ware.vo.LockStockResult;
import com.example.gulimall.ware.vo.OrderItemVo;
import com.example.gulimall.ware.vo.SkuHasStockVo;
import com.example.gulimall.ware.vo.WareSkuLockVo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.gulimall.ware.dao.WareSkuDao;
import com.example.gulimall.ware.entity.WareSkuEntity;
import com.example.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareSkuDao wareSkuDao;

    @Autowired
    ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        // 1、判断如果还没有库存记录   新增
        List<WareSkuEntity> wareSkuEntityList = this.baseMapper.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (wareSkuEntityList == null || wareSkuEntityList.size() == 0) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            // TODO 远程查询sku的名字 , 如果失败整个事务无需回滚
            //
            try {
                R info = productFeignService.info(skuId);
                if (info.getCode() == 0) {
                    Map<String, Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                    wareSkuEntity.setSkuName((String) skuInfo.get("skuName"));
                }
            } catch (Exception e) {

            }

            this.baseMapper.insert(wareSkuEntity);
        } else {
            // 2、如果有了库存记录     更新
            this.baseMapper.addStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVo vo = new SkuHasStockVo();
            // 查询当前sku的总库存量  SELECT SUM(stock - stock_locked) FROM wms_ware_sku WHERE sku_id = ?;
            Long count = baseMapper.getSkuStock(skuId);
            vo.setSkuId(skuId);
            vo.setHasStock(count == null ? false : count > 0);
            return vo;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * @Transactional(rollbackFor = NoStockException.class)  只要抛出NoStockException异常，就回滚
     * @param vo
     * @return
     */
    @Transactional  // 默认只要是运行时异常，都会回滚
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {
        // 1、按照下单的收货地址，找到就近仓库 锁定库存 （不这么麻烦了）
        // 1、找到每个商品在哪个仓库都有库存
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            // 查询这个商品在哪里有库存
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(skuId);
            stock.setWareIds(wareIds);
            stock.setNum(item.getCount());
            return stock;
        }).collect(Collectors.toList());

        Boolean allLocked = true;
        // 2、锁定库存
        for (SkuWareHasStock hasStock : collect) {
            Boolean skuStocked = false;  // 默认某件商品没有被锁住
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareIds();
            if (wareIds == null || wareIds.size() == 0) {
                // 没有任何仓库中有这个商品的库存
                throw new NoStockException(skuId);
            }
            for (Long wareId : wareIds) {
                // 锁定库存  UPDATE wms_ware_sku SET stock_locked = stock_locked + #{num} WHERE sku_id = #{skuId} AND ware_id = #{wareId} AND stock - stock_locked >= #{num}
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, hasStock.getNum());
                if (count == 0) {
                    // 当前仓库锁定库存失败，重试下一个仓库
                } else {
                    // 锁定库存成功，就没有必要再去其他仓库锁这件商品了
                    skuStocked = true;
                    break;
                }
            }
            if (!skuStocked){
                // 当前商品在所有仓库中都没有被锁住
                throw new NoStockException(skuId);
            }

        }

        // 能走到这里，肯定就是全部锁定成功

        return true;
    }


    /**
     * 商品在哪个仓库都有库存
     */
    @Data
    class SkuWareHasStock {
        /**
         * 哪个商品
         */
        private Long skuId;
        /**
         * 锁多少件这个商品
         */
        private Integer num;
        /**
         * 这个商品在那些仓库中存在
         */
        private List<Long> wareIds;
    }

}