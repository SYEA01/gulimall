package com.example.gulimall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author taoao
 */
@SpringBootApplication
@EnableDiscoveryClient  // 开启服务注册发现功能
@EnableFeignClients  // 开启远程调用功能
@EnableRedisHttpSession  // 整合Redis，开启SpringSession
public class GulimallAuthServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallAuthServerApplication.class, args);
    }
}
