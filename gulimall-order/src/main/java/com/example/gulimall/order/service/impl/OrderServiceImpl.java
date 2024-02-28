package com.example.gulimall.order.service.impl;

import com.example.common.vo.MemberRespVo;
import com.example.gulimall.order.feign.CartFeignService;
import com.example.gulimall.order.feign.MemberFeignService;
import com.example.gulimall.order.interceptor.LoginUserInterceptor;
import com.example.gulimall.order.vo.MemberAddressVo;
import com.example.gulimall.order.vo.OrderConfirmVo;
import com.example.gulimall.order.vo.OrderItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.gulimall.order.dao.OrderDao;
import com.example.gulimall.order.entity.OrderEntity;
import com.example.gulimall.order.service.OrderService;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    CartFeignService cartFeignService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        // 1、远程查询所有的收货地址列表
        List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
        confirmVo.setAddress(address);

        // 2、远程查询购物车所有选中的购物项
        List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
        confirmVo.setItems(items);

        // 3、查询用户积分
        Integer integration = memberRespVo.getIntegration();
        confirmVo.setIntegration(integration);

        // 4、其他数据自动计算

        // TODO 5、防重令牌


        return confirmVo;
    }

}