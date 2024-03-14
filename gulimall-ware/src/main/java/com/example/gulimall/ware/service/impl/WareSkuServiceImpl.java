package com.example.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.exception.NoStockException;
import com.example.common.to.mq.StockDetailTo;
import com.example.common.to.mq.StockLockedTo;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;
import com.example.common.utils.R;
import com.example.gulimall.ware.dao.WareSkuDao;
import com.example.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.example.gulimall.ware.entity.WareOrderTaskEntity;
import com.example.gulimall.ware.entity.WareSkuEntity;
import com.example.gulimall.ware.feign.OrderFeignService;
import com.example.gulimall.ware.feign.ProductFeignService;
import com.example.gulimall.ware.service.WareOrderTaskDetailService;
import com.example.gulimall.ware.service.WareOrderTaskService;
import com.example.gulimall.ware.service.WareSkuService;
import com.example.gulimall.ware.vo.OrderItemVo;
import com.example.gulimall.ware.vo.OrderVo;
import com.example.gulimall.ware.vo.SkuHasStockVo;
import com.example.gulimall.ware.vo.WareSkuLockVo;
import com.rabbitmq.client.Channel;
import lombok.Data;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareSkuDao wareSkuDao;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    WareOrderTaskService wareOrderTaskService;

    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    OrderFeignService orderFeignService;

    @Autowired
    RabbitTemplate rabbitTemplate;


    /**
     * 解锁库存
     *
     * @param skuId
     * @param wareId
     * @param num
     * @param taskDetailId
     */
    private void unLockStock(Long skuId, Long wareId, Integer num, Long taskDetailId) {
        // 库存解锁
        // UPDATE wms_ware_sku SET stock_locked = stock_locked-#{num} WHERE sku_id = #{skuId} AND ware_id = #{wareId}
        wareSkuDao.unLockStock(skuId, wareId, num);

        // 更新库存工作单详情的状态
        WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
        entity.setId(taskDetailId);
        entity.setLockStatus(2);  // 变为已解锁
        wareOrderTaskDetailService.updateById(entity);
    }

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
     * @param vo 库存解锁的场景：
     *           1）、下订单成功，订单过期没有支付，被系统自动取消；被用户手动取消。都要解锁库存
     *           2）、下订单成功，库存锁定成功，但是后面的业务调用失败，导致订单回滚
     *           之前锁定的库存就要自动解锁
     * @return
     * @Transactional(rollbackFor = NoStockException.class)  只要抛出NoStockException异常，就回滚
     */
    @Transactional  // 默认只要是运行时异常，都会回滚
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {
        /**
         * 保存库存工作单的详情
         * 追溯。
         */
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSn());
        wareOrderTaskService.save(taskEntity);

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
            // 1、如果每一个商品都锁定成功，将当前商品锁定了几件的工作单详情记录发送给MQ
            // 2、如果锁定失败，前面保存的工作单信息就回滚了。发送出去的消息，即使要解锁记录，由于去数据库查不到id，所以也就不用解锁 （其实不合理）
            for (Long wareId : wareIds) {
                // 锁定库存  UPDATE wms_ware_sku SET stock_locked = stock_locked + #{num} WHERE sku_id = #{skuId} AND ware_id = #{wareId} AND stock - stock_locked >= #{num}
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, hasStock.getNum());
                if (count == 0) {
                    // 当前仓库锁定库存失败，重试下一个仓库
                } else {
                    // 锁定库存成功，就没有必要再去其他仓库锁这件商品了
                    skuStocked = true;
                    // TODO 告诉MQ库存锁定成功
                    // 锁定成功之后，锁定成功的详情
                    WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity(null, skuId, null, hasStock.getNum(), taskEntity.getId(), wareId, 1);
                    wareOrderTaskDetailService.save(wareOrderTaskDetailEntity);
                    StockLockedTo stockLockedTo = new StockLockedTo();
                    stockLockedTo.setId(taskEntity.getId());
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    BeanUtils.copyProperties(wareOrderTaskDetailEntity, stockDetailTo);
                    // 只发工作单详情的id不行，防止回滚以后找不到数据
                    stockLockedTo.setDetail(stockDetailTo);
                    rabbitTemplate.convertAndSend("stock.event.exchange", "stock.locked", stockLockedTo);
                    break;
                }
            }
            if (!skuStocked) {
                // 当前商品在所有仓库中都没有被锁住
                throw new NoStockException(skuId);
            }

        }

        // 能走到这里，肯定就是全部锁定成功

        return true;
    }

    @Override
    public void unLockStock(StockLockedTo to) {
        StockDetailTo detail = to.getDetail();
        Long skuId = detail.getSkuId();
        Long detailId = detail.getId();  // 库存工作单详情id
        // 解锁
        // 1、查询数据库关于这个订单的锁定库存详情信息
        //  有：证明库存锁定成功了
        //      要不要解锁还要看订单情况
        //           1）、没有这个订单，必须解锁
        //           2）、有这个订单，不是解锁库存
        //                  看订单状态：已取消，解锁库存
        //                            没取消，不能解锁
        //  没有：库存锁定失败了，库存回滚了  这种情况无需解锁
        WareOrderTaskDetailEntity detailServiceById = wareOrderTaskDetailService.getById(detailId);
        if (detailServiceById != null) {
            // 解锁
            Long id = to.getId();  // 库存工作单的id
            WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(id);
            String orderSn = taskEntity.getOrderSn();  // 订单号
            // 根据订单号查询订单的状态
            R r = orderFeignService.getOrderStatus(orderSn);
            if (r.getCode() == 0) {
                // 订单数据返回成功
                OrderVo orderVo = r.getData(new TypeReference<OrderVo>() {
                });
                if (orderVo == null || orderVo.getStatus() == 4) {
                    // 订单不存在，必须解锁
                    // 订单已经被取消了，可以解锁库存
                    if (detailServiceById.getLockStatus() ==1) {
                        // 当前库存工作单详情的状态是1，已锁定， 才可以解锁库存
                        unLockStock(skuId, detail.getWareId(), detail.getSkuNum(), detailId);
                    }
                }
            } else {
                // 远程调用失败，拒绝收到消息，将消息重新放回队列中，让别人继续消费，解锁
                throw new RuntimeException("远程服务失败");
            }
        } else {
            // 无需解锁
        }

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