package com.example.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.common.exception.NoStockException;
import com.example.common.to.mq.OrderTo;
import com.example.common.to.mq.SecKillOrderTo;
import com.example.common.utils.R;
import com.example.common.vo.MemberRespVo;
import com.example.gulimall.order.constant.OrderConstant;
import com.example.gulimall.order.entity.OrderItemEntity;
import com.example.gulimall.order.entity.PaymentInfoEntity;
import com.example.gulimall.order.enume.OrderStatusEnum;
import com.example.gulimall.order.feign.CartFeignService;
import com.example.gulimall.order.feign.MemberFeignService;
import com.example.gulimall.order.feign.ProductFeignService;
import com.example.gulimall.order.feign.WareFeignService;
import com.example.gulimall.order.interceptor.LoginUserInterceptor;
import com.example.gulimall.order.service.OrderItemService;
import com.example.gulimall.order.service.PaymentInfoService;
import com.example.gulimall.order.to.OrderCreateTo;
import com.example.gulimall.order.vo.*;
//import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.gulimall.order.dao.OrderDao;
import com.example.gulimall.order.entity.OrderEntity;
import com.example.gulimall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVo> submitVoThreadLocal = new ThreadLocal<>();

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    PaymentInfoService paymentInfoService;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        System.out.println("主线程---》" + Thread.currentThread().getId());

        // 获取主线程的请求数据，然后在异步时每一个线程中都设置上这个请求数据，就可以保证不同的线程都可以获取到当前线程的请求数据了
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
            // 1、远程查询所有的收货地址列表
            System.out.println("member线程---》" + Thread.currentThread().getId());
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
            confirmVo.setAddress(address);
        }, executor);

        CompletableFuture<Void> getCartFuture = CompletableFuture.runAsync(() -> {
            // 2、远程查询购物车所有选中的购物项
            System.out.println("cart线程---》" + Thread.currentThread().getId());
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
            confirmVo.setItems(items);
        }, executor).thenRunAsync(() -> {
            List<OrderItemVo> items = confirmVo.getItems();
            List<Long> ids = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            R r = wareFeignService.getSkusHasStock(ids);
            List<SkuStockVo> data = r.getData(new TypeReference<List<SkuStockVo>>() {
            });
            if (data != null) {
                Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                confirmVo.setStocks(map);
            }
        }, executor);


        // feign在远程调用之前要构造请求，调用很多的拦截器RequestInterceptor

        // 3、查询用户积分
        Integer integration = memberRespVo.getIntegration();
        confirmVo.setIntegration(integration);

        // 4、其他数据自动计算

        // TODO 5、防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        // 给服务器放一个防重令牌  过期时间：30分钟
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId(), token, 30, TimeUnit.MINUTES);

        // 给页面放一个防重令牌
        confirmVo.setOrderToken(token);

        CompletableFuture.allOf(getAddressFuture, getCartFuture).get();


        return confirmVo;
    }

    // 本地事务，在分布式系统下，只能控制住自己的回滚。控制不了其他服务的回滚
    // 应该用分布式事务
//    @GlobalTransactional  // seata的全局事务注解
    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        submitVoThreadLocal.set(vo);
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        responseVo.setCode(0);
        // 1、验证令牌 【令牌的对比和删除必须保证原子性】
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = vo.getOrderToken();
        String tokenKey = OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId();
//        String serverToken = redisTemplate.opsForValue().get(tokenKey);
//        if (orderToken != null && orderToken.equals(serverToken)) {
//            // 令牌验证通过
//        } else {
//            // 不通过
//        }
        // 原子验证令牌和删除令牌     0代表令牌校验失败；1删除成功
        Long execute = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(tokenKey), orderToken);
        if (execute == 0L) {
            // 令牌验证失败
            responseVo.setCode(1);
            return responseVo;
        } else {
            // 令牌验证通过
            // 下单去创建订单，验令牌，验价格，锁库存。。。
            // 1、创建订单、订单项等信息
            OrderCreateTo orderCreateTo = createOrder();
            // 2、验价
            BigDecimal payAmount = orderCreateTo.getOrder().getPayAmount();  // 订单算出的价格
            BigDecimal viewPrice = vo.getPayPrice();  // 页面提交过来的价格
            if (Math.abs(payAmount.subtract(viewPrice).doubleValue()) < 0.01) {
                // 金额对比成功
                // TODO 3、保存订单
                saveOrder(orderCreateTo);
                // 4、锁定库存,只要有异常，就回滚订单数据。
                // 订单号、所有订单项（skuId，skuName，锁定的商品数量）
                WareSkuLockVo lockVo = new WareSkuLockVo();
                lockVo.setOrderSn(orderCreateTo.getOrder().getOrderSn());
                List<OrderItemVo> locks = orderCreateTo.getOrderItems().stream().map(item -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    orderItemVo.setSkuId(item.getSkuId());
                    orderItemVo.setCount(item.getSkuQuantity());
                    orderItemVo.setTitle(item.getSkuName());
                    return orderItemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(locks);
                // TODO 4、远程锁库存
                // 为了保证高并发，库存服务自己回滚。  可以发消息给库存服务。
                // 库存服务本身也可以使用自动解锁模式    使用消息队列
                R r = wareFeignService.orderLockStock(lockVo);
                if (r.getCode() == 0) {
                    //锁定成功了
                    responseVo.setOrder(orderCreateTo.getOrder());
                    // TODO 5、远程扣减积分
//                    int i = 10 / 0;  // 这里出了问题，订单会回滚，远程锁库存不会回滚
                    // TODO 订单创建成功，发送消息给MQ
                    rabbitTemplate.convertAndSend("order.event.exchange", "order.create.order", orderCreateTo.getOrder());
                    return responseVo;
                } else {
                    // 锁定失败了
                    String msg = (String) r.get("msg");
                    throw new NoStockException(msg);
//                    responseVo.setCode(3);
//                    return responseVo;
                }
            } else {
                responseVo.setCode(2);
                return responseVo;
            }


        }
    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        OrderEntity orderEntity = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        return orderEntity;
    }

    @Override
    public void closeOrder(OrderEntity entity) {
        // 先去查询当前这个订单的最新状态
        OrderEntity orderEntity = this.getById(entity.getId());
        if (OrderStatusEnum.CREATE_NEW.getCode().equals(orderEntity.getStatus())) {
            // 如果当前订单的状态是待付款，才可以关单
            OrderEntity update = new OrderEntity();
            update.setId(orderEntity.getId());
            update.setStatus(OrderStatusEnum.CANCLED.getCode());
            this.updateById(update);
            // TODO 发给MQ一个订单解锁成功消息
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(orderEntity, orderTo);
            try {
                // TODO 保证消息一定会发送出去，每一个消息都可以做好日志记录（给数据库保存每一个消息的详细信息）
                // TODO 定期扫描数据库，将失败的消息再发送一遍；
                rabbitTemplate.convertAndSend("order.event.exchange", "order.release.other", orderTo);
            } catch (AmqpException e) {
                // TODO 将没发送成功的消息，进行重试发送
            }
        }
    }

    @Override
    public PayVo getOrderPay(String orderSn) {
        PayVo payVo = new PayVo();
        OrderEntity order = this.getOrderByOrderSn(orderSn);
        // 取出金额，设置小数点后2位
        BigDecimal decimal = order.getPayAmount().setScale(2, BigDecimal.ROUND_UP);
        payVo.setTotalAmount(decimal.toString());
        payVo.setOutTradeNo(order.getOrderSn());
        List<OrderItemEntity> orderItemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
        OrderItemEntity itemEntity = orderItemEntities.get(0);
        payVo.setSubject(itemEntity.getSkuName());  // 订单的标题
        payVo.setBody(itemEntity.getSkuAttrsVals());  // 订单的备注
        return payVo;
    }

    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id", memberRespVo.getId()).orderByDesc("id")
        );

        List<OrderEntity> collect = page.getRecords().stream().map(order -> {
            List<OrderItemEntity> orderItemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
            order.setItemEntities(orderItemEntities);
            return order;
        }).collect(Collectors.toList());

        page.setRecords(collect);

        return new PageUtils(page);
    }

    @Override
    public String handlePayResult(PayAsyncVo vo) {
        // 1、保存交易流水
        PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
        paymentInfoEntity.setAlipayTradeNo(vo.getTrade_no());
        paymentInfoEntity.setOrderSn(vo.getOut_trade_no());
        paymentInfoEntity.setPaymentStatus(vo.getTrade_status());
        paymentInfoEntity.setCallbackTime(vo.getNotify_time());
        paymentInfoService.save(paymentInfoEntity);

        // 2、修改订单的状态信息
        if (vo.getTrade_status().equals("TRADE_SUCCESS") || vo.getTrade_status().equals("TRADE_FINISHED")) {
            // 支付成功状态
            String outTradeNo = vo.getOut_trade_no();
            this.baseMapper.updateOrderStatus(outTradeNo, OrderStatusEnum.PAYED.getCode());
        }

        return "success";
    }

    @Override
    public void createSecKillOrder(SecKillOrderTo to) {
        // TODO 保存订单信息
        OrderEntity orderEntity = new OrderEntity();
        String orderSn = to.getOrderSn();
        orderEntity.setOrderSn(orderSn);
        orderEntity.setMemberId(to.getMemberId());

        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        Integer num = to.getNum();
        BigDecimal price = to.getSeckillPrice().multiply(BigDecimal.valueOf(num));
        orderEntity.setPayAmount(price);

        this.save(orderEntity);

        // TODO 保存订单项信息
        OrderItemEntity itemEntity = new OrderItemEntity();
        itemEntity.setOrderSn(orderSn);
        itemEntity.setRealAmount(price);
        // TODO 获取当前SKU的详细信息进行设置
        itemEntity.setSkuQuantity(num);

        orderItemService.save(itemEntity);
    }

    /**
     * 保存订单数据
     *
     * @param orderCreateTo
     */
    private void saveOrder(OrderCreateTo orderCreateTo) {
        // 保存订单数据
        OrderEntity order = orderCreateTo.getOrder();
        order.setModifyTime(new Date());
        this.save(order);

        // 保存订单项数据
        List<OrderItemEntity> orderItems = orderCreateTo.getOrderItems();
        orderItemService.saveBatch(orderItems);


    }

    /**
     * 创建订单
     */
    private OrderCreateTo createOrder() {
        OrderCreateTo orderCreateTo = new OrderCreateTo();
        // 1、生成订单号
        String orderSn = IdWorker.getTimeId();
        OrderEntity order = buildOrder(orderSn);
        orderCreateTo.setOrder(order);

        // 2、获取所有的订单项
        List<OrderItemEntity> itemEntities = buildOrderItems(orderSn);
        orderCreateTo.setOrderItems(itemEntities);
        // 3、计算价格相关
        computePrice(order, itemEntities);


        return orderCreateTo;

    }

    /**
     * 计算价格相关
     *
     * @param order
     * @param itemEntities
     */
    private void computePrice(OrderEntity order, List<OrderItemEntity> itemEntities) {
        // 1、订单价格相关  订单的总额 = 叠加每一个订单项的总额
        // 订单的总额
        BigDecimal totalPrice = new BigDecimal("0.0");
        // 优惠卷的总额
        BigDecimal coupon = new BigDecimal("0.0");
        // 积分的总额
        BigDecimal integration = new BigDecimal("0.0");
        // 促销的总额
        BigDecimal promotion = new BigDecimal("0.0");
        // 积分信息
        BigDecimal gift = new BigDecimal("0.0");
        // 成长值
        BigDecimal growth = new BigDecimal("0.0");
        for (OrderItemEntity entity : itemEntities) {
            totalPrice = totalPrice.add(entity.getRealAmount());
            coupon = coupon.add(entity.getCouponAmount());
            integration = integration.add(entity.getIntegrationAmount());
            promotion = promotion.add(entity.getPromotionAmount());
            gift = gift.add(BigDecimal.valueOf(entity.getGiftIntegration()));
            growth = growth.add(BigDecimal.valueOf(entity.getGiftGrowth()));
        }
        order.setTotalAmount(totalPrice);
        // 应付的总额 = 订单总额 + 运费
        order.setPayAmount(totalPrice.add(order.getFreightAmount()));

        order.setPromotionAmount(promotion);
        order.setIntegrationAmount(integration);
        order.setCouponAmount(coupon);

        // 2、订单的相关状态信息
        order.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        order.setDeleteStatus(0);  // 未删除
        // 3、自动确认时间
        order.setAutoConfirmDay(7);

        // 4、积分、成长值信息
        order.setIntegration(gift.intValue());
        order.setGrowth(growth.intValue());


    }

    private OrderEntity buildOrder(String orderSn) {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        OrderEntity entity = new OrderEntity();

        entity.setOrderSn(orderSn);
        entity.setMemberId(memberRespVo.getId());
        // 2、获取收货地址信息
        OrderSubmitVo orderSubmitVo = submitVoThreadLocal.get();
        R r = wareFeignService.getFare(orderSubmitVo.getAddrId());
        FareVo fareResp = r.getData(new TypeReference<FareVo>() {
        });
        // 设置运费信息
        entity.setFreightAmount(fareResp.getFare());
        // 设置收货人信息
        entity.setReceiverCity(fareResp.getAddress().getCity());
        entity.setReceiverDetailAddress(fareResp.getAddress().getDetailAddress());
        entity.setReceiverName(fareResp.getAddress().getName());
        entity.setReceiverPhone(fareResp.getAddress().getPhone());
        entity.setReceiverPostCode(fareResp.getAddress().getPostCode());
        entity.setReceiverProvince(fareResp.getAddress().getProvince());
        entity.setReceiverRegion(fareResp.getAddress().getRegion());

        return entity;
    }

    /**
     * 构建所有订单项数据
     *
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
        if (currentUserCartItems != null && currentUserCartItems.size() > 0) {
            List<OrderItemEntity> itemEntities = currentUserCartItems.stream().map(cartItem -> {
                // 构建订单项
                OrderItemEntity itemEntity = buildOrderItem(cartItem);
                itemEntity.setOrderSn(orderSn);
                return itemEntity;
            }).collect(Collectors.toList());
            return itemEntities;
        }
        return null;
    }

    /**
     * 构建某一个订单项
     *
     * @param cartItem
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo cartItem) {
        OrderItemEntity itemEntity = new OrderItemEntity();
        // 1、订单信息：订单号
        // 2、商品的spu信息
        Long skuId = cartItem.getSkuId();
        R r = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo spuInfoVo = r.getData(new TypeReference<SpuInfoVo>() {
        });
        itemEntity.setSpuId(spuInfoVo.getId());
        itemEntity.setSpuBrand(spuInfoVo.getBrandId().toString());
        itemEntity.setSpuName(spuInfoVo.getSpuName());
        itemEntity.setCategoryId(spuInfoVo.getCatalogId());
        // 3、商品的sku信息
        itemEntity.setSkuId(skuId);
        itemEntity.setSkuName(cartItem.getTitle());
        itemEntity.setSkuPic(cartItem.getImage());
        itemEntity.setSkuPrice(cartItem.getPrice());
        String skuAttrs = StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";");
        itemEntity.setSkuAttrsVals(skuAttrs);
        itemEntity.setSkuQuantity(cartItem.getCount());
        // 4、优惠信息（没做）
        // 5、积分信息
        itemEntity.setGiftGrowth(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getCount())).intValue());
        itemEntity.setGiftIntegration(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getCount())).intValue());

        // 6、订单项的价格信息
        itemEntity.setPromotionAmount(new BigDecimal("0.0"));
        itemEntity.setCouponAmount(new BigDecimal("0.0"));
        itemEntity.setIntegrationAmount(new BigDecimal("0.0"));
        BigDecimal originalPrice = itemEntity.getSkuPrice().multiply(BigDecimal.valueOf(itemEntity.getSkuQuantity()));
        // 实际价格 总额 - 各种优惠
        BigDecimal actualPrice = originalPrice.subtract(itemEntity.getCouponAmount())
                .subtract(itemEntity.getPromotionAmount())
                .subtract(itemEntity.getIntegrationAmount());
        itemEntity.setRealAmount(actualPrice);

        return itemEntity;
    }

}