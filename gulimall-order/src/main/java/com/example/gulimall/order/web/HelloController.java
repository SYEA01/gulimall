package com.example.gulimall.order.web;

import com.example.gulimall.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.UUID;

/**
 * @author taoao
 */
@Controller
public class HelloController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @ResponseBody
    @GetMapping("/test/createOrder")
    public String createOrderTest() {
        // 假设订单下单成功
        OrderEntity entity = new OrderEntity();
        entity.setOrderSn(UUID.randomUUID().toString());
        entity.setModifyTime(new Date());
        System.out.println("entity = " + entity);

        // 给MQ发送消息
        // 给哪一个交换机发消息、路由键、发送的消息内容
        rabbitTemplate.convertAndSend("order.event.exchange", "order.create.order", entity);
        return "ok";
    }

    @GetMapping("/{page}.html")
    public String listPage(@PathVariable String page) {
        return page;
    }

}
