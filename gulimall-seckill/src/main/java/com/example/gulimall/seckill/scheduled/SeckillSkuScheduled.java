package com.example.gulimall.seckill.scheduled;

import com.example.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 定时上架秒杀商品
 * 每天晚上3点，上架最近3天需要秒杀的商品
 * 当天00:00:00  - 23:59:59
 * 明天00:00:00  - 23:59:59
 * 后天00:00:00  - 23:59:59
 *
 * @author taoao
 */
@Slf4j
@Service
public class SeckillSkuScheduled {

    @Autowired
    SeckillService seckillService;

    @Autowired
    RedissonClient redissonClient;

    private final String UPLOAD_LOCK = "seckill:upload:lock";

    /**
     * TODO 幂等性处理，已经上架过了的，就不用再次上架了
     * 每天晚上3点，上架最近3天需要秒杀的商品
     */
    @Scheduled(cron = "*/3 * * * * ?")
    public void uploadSeckillSkuLatest3Days() {
        // 1、重复上架无需处理
        log.info("上架秒杀的商品信息。。。");
        // 加一个分布式锁，多个定时服务 只有一个可以执行定时任务
        // 得到锁的服务才能往下继续执行
        // 锁的业务执行完成，状态已经更新完成，释放锁以后，其他服务获取到就会拿到最新的状态（保证原子性，一个服务执行的时候，不会被多部署的其他分布式服务打扰）
        RLock lock = redissonClient.getLock(UPLOAD_LOCK);
        lock.lock(10, TimeUnit.SECONDS);  // 锁10秒

        try {
            seckillService.uploadSeckillSkuLatest3Days();
        } finally {
            lock.unlock();
        }
    }
}
