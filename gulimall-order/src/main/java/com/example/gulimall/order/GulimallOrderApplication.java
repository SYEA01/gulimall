package com.example.gulimall.order;

import com.alibaba.cloud.seata.GlobalTransactionAutoConfiguration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author taoao
 */
@SpringBootApplication(exclude = GlobalTransactionAutoConfiguration.class)  // 排除全局事务
@EnableDiscoveryClient
@EnableRabbit  // 开启RabbitMQ
@EnableRedisHttpSession  // 开启SpringSession
@EnableFeignClients  // 开启远程调用
public class GulimallOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }
}
