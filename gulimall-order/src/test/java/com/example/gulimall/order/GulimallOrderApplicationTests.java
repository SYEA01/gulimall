package com.example.gulimall.order;

import com.example.gulimall.order.entity.OrderEntity;
import com.example.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.UUID;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GulimallOrderApplicationTests {

    /**
     * 可以帮助我们创建队列、绑定关系等等
     */
    @Autowired
    AmqpAdmin amqpAdmin;

    /**
     * 可以帮助我们发送消息
     */
    @Autowired
    RabbitTemplate rabbitTemplate;


    /**
     * 1、如何创建Exchange、Queue、Binding
     * 1）、使用AmqpAdmin进行创建
     * 2、如何收发消息
     * 1）、
     */
    @Test
    public void createExchange() {
        // 创建交换机  交换机名字叫：hello.java.exchange  是否持久化：是   是否自动删除：不
        DirectExchange directExchange = new DirectExchange("hello.java.exchange", true, false);
        // 利用amqpAdmin声明交换机
        amqpAdmin.declareExchange(directExchange);
        log.info("exchange[{}] create success", "hello.java.exchange");
    }

    @Test
    public void createQueue() {
        // 创建一个队列 队列名字叫：hello.java.queue  是否持久化：是   是否排他：不（可以被其他连接连接）   是否自动删除：不
        Queue queue = new Queue("hello.java.queue", true, false, false);
        // 声明队列
        amqpAdmin.declareQueue(queue);
        log.info("queue[{}] create success", "hello.java.queue");
    }

    @Test
    public void createBinding() {
        // 交换机与队列建立绑定关系 【将exchange指定的交换机和destination目的地进行绑定，使用routingKey作为指定的路由键】
        // 【参数】：目的地：（队列）   目的地类型：（跟队列还是交换机进行绑定）   交换机：   路由键：   自定义参数：
        Binding binding = new Binding("hello.java.queue", Binding.DestinationType.QUEUE, "hello.java.exchange", "hello.java", null);
        amqpAdmin.declareBinding(binding);
        log.info("binding success");
    }

    /**
     * 测试发送消息
     */
    @Test
    public void sendMessageTest() {

        // 1、如果发送的消息是个对象，这个对象必须序列化，才能将对象写出去
        // 2、发送的对象类型的消息，可以是JSON类型（不使用默认的Java序列化）


        for (int i = 0; i < 10; i++) {
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
                rabbitTemplate.convertAndSend("hello.java.exchange", "hello.java", entity);
            }
            log.info("message send success");
        }

    }

}
