package com.example.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author taoao
 */
@Configuration
public class MyRedissonConfig {

    /**
     * 所有对Redisson 的使用都是使用RedissonClient 对象
     *
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() {
        // 1、创建配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://gulimall-redis.gulimall:6379");
        // 2、根据config创建出 RedissonClient 实例
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
