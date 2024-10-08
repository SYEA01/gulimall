package com.example.gulimall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author taoao
 */
@EnableRedisHttpSession  // 开启SpringSession
@EnableFeignClients  // 开启远程调用
@EnableDiscoveryClient  // 开启服务注册与发现
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class GulimallCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallCartApplication.class, args);
    }
}
