package com.example.gulimall.ware;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author taoao
 */
@SpringBootApplication
@EnableDiscoveryClient  // 开启服务的注册发现功能
@EnableFeignClients  // 开启Feign远程调用功能
@EnableRabbit  // 开启RabbitMQ
public class GulimallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallWareApplication.class, args);
    }

}
