package com.example.gulimall.order.dao;

import com.example.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 * 
 * @author tpc
 * @email tpc@gmail.com
 * @date 2023-08-02 17:30:11
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    /**
     * 修改订单的状态信息
     * @param outTradeNo
     * @param code
     */
    void updateOrderStatus(@Param("outTradeNo") String outTradeNo, @Param("code") Integer code);
}
