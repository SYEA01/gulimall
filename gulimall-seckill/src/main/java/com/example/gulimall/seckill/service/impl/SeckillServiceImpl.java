package com.example.gulimall.seckill.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.common.to.mq.SecKillOrderTo;
import com.example.common.utils.R;
import com.example.common.vo.MemberRespVo;
import com.example.gulimall.seckill.feign.CouponFeignService;
import com.example.gulimall.seckill.feign.ProductFeignService;
import com.example.gulimall.seckill.interceptor.LoginUserInterceptor;
import com.example.gulimall.seckill.service.SeckillService;
import com.example.gulimall.seckill.to.SecKillSkuRedisTo;
import com.example.gulimall.seckill.vo.SeckillSessionsWithSkus;
import com.example.gulimall.seckill.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author taoao
 */
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    RabbitTemplate rabbitTemplate;

    // 缓存 活动的key的前缀
    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";

    // 缓存 秒杀商品的key的前缀
    private final String SKUKILL_CACHE_PREFIX = "seckill:skus";

    // 缓存 秒杀商品的分布式信号量的前缀
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";  // + 商品随机码

    @Override
    public void uploadSeckillSkuLatest3Days() {
        // 1、去扫描最近三天需要参与秒杀的活动
        R session = couponFeignService.getLatest3DaySession();
        if (session.getCode() == 0) {
            //远程调用成功，上架商品
            List<SeckillSessionsWithSkus> sessionData = session.getData(new TypeReference<List<SeckillSessionsWithSkus>>() {
            });
            if (sessionData != null && sessionData.size() > 0) {
                // 缓存到redis
                // 1、保存秒杀活动信息、
                saveSessionInfos(sessionData);
                // 2、以及保存秒杀商品信息
                saveSessionSkuInfos(sessionData);
            }

        }

    }

    /**
     * 这是getCurrentSeckillSkusResource资源的限流降级方法
     * @param e
     * @return
     */
    public List<SecKillSkuRedisTo> ziDingYiBlockHandler(BlockException e){
        log.error("getCurrentSeckillSkusResource资源被限流了，这是自定义降级方法。。。");
        return null;
    }

    // 方式2、使用@SentinelResource("自定义资源名称")注解自定义受保护资源
    @SentinelResource(value = "getCurrentSeckillSkusResource",blockHandler = "ziDingYiBlockHandler")
    @Override
    public List<SecKillSkuRedisTo> getCurrentSeckillSkus() {
        // 1、确定当前时间 属于哪个秒杀场次
        // 当前时间
        long time = new Date().getTime();

        /**
         * 1、自定义一段受保护的资源【 包裹任意代码 】：SphU.entry("自定义资源名字")
         *      try (Entry entry = SphU.entry("seckillSkus")) {
         *      }
         * 2、可以在Sentinel控制台对这段资源进行限流、熔断降级
         *      如果这段资源被限流、熔断降级了，就会执行cache中的代码
         *          } catch (BlockException e) {
         *             // 被限流了，就会抛出BlockException异常，然后执行这个里面的代码
         *
         *         }
         */
        try (Entry entry = SphU.entry("seckillSkus")) {
            // 去redis中查询所有的以 seckill:sessions: 开头的场次信息
            Set<String> keys = redisTemplate.keys(SESSIONS_CACHE_PREFIX + "*");
            for (String key : keys) {
                // key格式示例：seckill:sessions:1711072800000_1711076400000
                String timeInterval = key.replace(SESSIONS_CACHE_PREFIX, "");  // 1711072800000_1711076400000
                String[] s = timeInterval.split("_");
                Long startTime = Long.parseLong(s[0]);  // 开始时间：1711072800000
                Long endTime = Long.parseLong(s[1]);  // 结束时间：1711076400000
                // 得到当前场次
                if (time >= startTime && time <= endTime) {
                    // 2、获取这个秒杀场次需要的所有商品信息
                    // 获取所有key以seckill:sessions: 开头的秒杀场次的value
                    List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                    // 获取所有key为seckill:skus 的秒杀商品信息
                    BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                    // 根据场次id获取商品信息 JSON
                    List<String> list = hashOps.multiGet(range);
                    if (list != null) {
                        List<SecKillSkuRedisTo> collect = list.stream().map(item -> {
                            // 将查询出的所有的商品（String格式）信息转换为to
                            SecKillSkuRedisTo killSkuRedisTo = JSON.parseObject(item, SecKillSkuRedisTo.class);
//                        killSkuRedisTo.setRandomCode(null);  // 当前秒杀开始了，需要用到随机码
                            return killSkuRedisTo;
                        }).collect(Collectors.toList());
                        return collect;
                    }
                    break;
                }
            }
        } catch (BlockException e) {
            // 被限流了，就会抛出BlockException异常，然后执行这个里面的代码
            log.error("资源被限流，{}", e.getMessage());

        }


        return null;
    }

    @Override
    public SecKillSkuRedisTo getSkuSeckillInfo(Long skuId) {
        // 1、找到所有需要参与秒杀的商品的key
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        Set<String> keys = hashOps.keys();
        if (keys != null && keys.size() > 0) {
            String regx = "\\d_" + skuId;
            for (String key : keys) {
                if (Pattern.matches(regx, key)) {  // 如果正则匹配上了
                    String skuJson = hashOps.get(key);
                    SecKillSkuRedisTo skuRedisTo = JSON.parseObject(skuJson, SecKillSkuRedisTo.class);
                    // 当前时间
                    long currentTime = new Date().getTime();
                    if (currentTime >= skuRedisTo.getStartTime() && currentTime <= skuRedisTo.getEndTime()) {
                        // 如果当前时间在某个秒杀活动时间段内
                        return skuRedisTo;
                    } else {
                        // 如果当前时间不在某个秒杀活动时间段内，不可以返回随机码
                        skuRedisTo.setRandomCode(null);
                    }
                    return skuRedisTo;
                }
            }
        }
        return null;
    }

    // TODO 秒杀商品的时候，每一个数据都有过期时间
    // TODO 秒杀后续的流畅，简化了收货地址等信息
    @Override
    public String kill(String killId, String key, Integer num) {
        long s1 = System.currentTimeMillis();

        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();

        // 1、获取当前秒杀商品的详细信息
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        String skuJson = hashOps.get(killId);
        if (StringUtils.isEmpty(skuJson)) {
            return null;
        } else {
            SecKillSkuRedisTo skuRedisTo = JSON.parseObject(skuJson, SecKillSkuRedisTo.class);
            // 2、校验合法性
            // 1)、秒杀时间过没过
            Long startTime = skuRedisTo.getStartTime();
            Long endTime = skuRedisTo.getEndTime();
            long currentTime = new Date().getTime();
            if (currentTime >= startTime && currentTime <= endTime) {
                // 时间合法
                // 2）、校验随机码和商品id
                String randomCode = skuRedisTo.getRandomCode();
                String id = skuRedisTo.getPromotionSessionId() + "_" + skuRedisTo.getSkuId();
                if (Objects.equals(randomCode, key) && Objects.equals(killId, id)) {
                    // 3）、验证购物的数量是否合理
                    if (num <= skuRedisTo.getSeckillLimit().intValue()) {
                        // 4）、验证这个人是否已经购买过。幂等性。 如果秒杀成功，就去占位。 userId_sessionId_skuId
                        // setnx
                        String userRedisKey = memberRespVo.getId() + "_" + id;
                        // 自动过期
                        long ttl = endTime - currentTime;
                        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(userRedisKey, num.toString(), ttl, TimeUnit.MILLISECONDS);
                        if (Boolean.TRUE.equals(aBoolean)) {
                            // 占位成功，说明这个人从来没买过  可以接着往下走了
                            // 获取信号量
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
                            // 信号量-num    （获取num个信号量）
                            boolean b = semaphore.tryAcquire(num);
                            if (Boolean.TRUE.equals(b)) {
                                // 秒杀成功
                                // 快速下单。直接发送MQ
                                String timeId = IdWorker.getTimeId();  // 创建一个订单号
                                SecKillOrderTo secKillOrderTo = new SecKillOrderTo();
                                secKillOrderTo.setOrderSn(timeId);
                                secKillOrderTo.setMemberId(memberRespVo.getId());
                                secKillOrderTo.setNum(num);
                                secKillOrderTo.setPromotionSessionId(skuRedisTo.getPromotionSessionId());
                                secKillOrderTo.setSkuId(skuRedisTo.getSkuId());
                                secKillOrderTo.setSeckillPrice(skuRedisTo.getSeckillPrice());
                                rabbitTemplate.convertAndSend("order.event.exchange", "order.seckill.order", secKillOrderTo);
                                long s2 = System.currentTimeMillis();
                                log.info("耗时：{}毫秒", (s2 - s1));
                                return timeId;
                            } else {
                                // 没有信号量了
                                return null;
                            }
                        } else {
                            // 失败代表这个人已经买过了
                            return null;
                        }
                    }
                } else {
                    // 随机码不通过
                    return null;
                }
            } else {
                // 时间不通过
                return null;
            }
        }

        return null;
    }

    /**
     * 保存秒杀活动信息
     *
     * @param sessions
     */
    private void saveSessionInfos(List<SeckillSessionsWithSkus> sessions) {
        sessions.forEach(session -> {
            // 开始时间和结束时间的时间戳
            Date date = session.getStartTime();
            long startTime = date.getTime();
            long endTime = session.getEndTime().getTime();
            // redis 中的key
            String key = SESSIONS_CACHE_PREFIX + startTime + "_" + endTime;

            // 幂等性  如果这个key存在了，就不用往redis中再次添加了
            Boolean hasKey = redisTemplate.hasKey(key);
            // 这个商品的所有商品的skuId
            // 往redis中存放秒杀活动信息
            if (Boolean.FALSE.equals(hasKey)) {  // 如果没有这个key，才往Redis中添加
                List<String> skuIds = session.getRelationSkus().stream().map(item -> item.getPromotionSessionId().toString() + "_" + item.getSkuId().toString()).collect(Collectors.toList());
                redisTemplate.opsForList().leftPushAll(key, skuIds);
            }
        });
    }

    /**
     * 保存秒杀商品信息
     *
     * @param sessions
     */
    private void saveSessionSkuInfos(List<SeckillSessionsWithSkus> sessions) {
        sessions.forEach(session -> {
            // 准备hash操作  key->value 结构的
            BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
            session.getRelationSkus().forEach(sku -> {
                // 4、 商品的随机码
                String randomCode = UUID.randomUUID().toString().replace("-", "");

                String redisSkuKey = sku.getPromotionSessionId().toString() + "_" + sku.getSkuId().toString();
                Boolean hasKeySku = ops.hasKey(redisSkuKey);
                if (Boolean.FALSE.equals(hasKeySku)) {

                    // 缓存秒杀商品信息
                    SecKillSkuRedisTo secKillSkuRedisTo = new SecKillSkuRedisTo();
                    // 1、sku的基本数据
                    R r = productFeignService.getSkuInfo(sku.getSkuId());
                    if (r.getCode() == 0) {
                        // 远程调用成功
                        SkuInfoVo info = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                        });
                        secKillSkuRedisTo.setSkuInfo(info);
                    }

                    // 2、sku的秒杀信息
                    BeanUtils.copyProperties(sku, secKillSkuRedisTo);
                    // 3、设置 当前商品的秒杀时间信息
                    secKillSkuRedisTo.setStartTime(session.getStartTime().getTime());
                    secKillSkuRedisTo.setEndTime(session.getEndTime().getTime());

                    secKillSkuRedisTo.setRandomCode(randomCode);

                    String s = JSON.toJSONString(secKillSkuRedisTo);
                    // 保存秒杀商品信息数据
                    ops.put(redisSkuKey, s);

                    // 如果当前这个场次的商品的库存信息已经上架，就不需要上架
                    // 5、TODO 每一个商品设置分布式信号量 （引入 Redisson分布式锁） 【 在Redis中扣 秒杀商品的库存用 】  **限流**
                    String redisSemaphoreKey = SKU_STOCK_SEMAPHORE + randomCode;
                    RSemaphore semaphore = redissonClient.getSemaphore(redisSemaphoreKey);
                    // 使用库存 设置信号量
                    semaphore.trySetPermits(sku.getSeckillCount().intValue());  // 商品可以秒杀的数量作为信号量
                }
            });
        });
    }

}
