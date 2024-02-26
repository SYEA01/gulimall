package com.example.gulimall.order.controller;

import com.example.gulimall.order.entity.OrderEntity;
import com.example.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

/**
 * @author taoao
 */
@Slf4j
@RestController
public class RabbitController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMq")
    public String sendMq(@RequestParam Integer num) {
        for (int i = 0; i < num; i++) {
            if (i % 2 == 0) {
                OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
                reasonEntity.setId(1L);
                reasonEntity.setCreateTime(new Date());
                reasonEntity.setName("哈哈 - " + i);
                // 给哪个交换机发送消息   路由键是什么   消息内容
                rabbitTemplate.convertAndSend("hello.java.exchange", "hello.java", reasonEntity);
            } else {
                OrderEntity entity = new OrderEntity();
                entity.setOrderSn(UUID.randomUUID().toString());
                // 给哪个交换机发送消息   路由键是什么   消息内容
                rabbitTemplate.convertAndSend("hello.java.exchange", "hello22.java", entity);
            }
            log.info("message send success");
        }
        return "ok";
    }
}
