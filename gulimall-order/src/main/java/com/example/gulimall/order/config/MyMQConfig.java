package com.example.gulimall.order.config;

import com.example.gulimall.order.entity.OrderEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author taoao
 */
@Configuration
public class MyMQConfig {



    // 直接使用@Bean 的方式 ，把Binding、Queue、Exchange放到Spring容器中，就会自动连接RabbitMQ，自动在RabbitMQ中创建出指定的交换机、队列，以及自动指定绑定关系

    /**
     * 创建队列 order.delay.queue，并指定延时队列需要的一系列参数
     *
     * @return
     */
    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "order.event.exchange");
        arguments.put("x-dead-letter-routing-key", "order.release.order");
        arguments.put("x-message-ttl", 60000);
        // 队列的名字、是不是持久化的、是不是排他的、是不是自动删除的、其他参数
        Queue queue = new Queue("order.delay.queue", true, false, false, arguments);
        return queue;
    }

    /**
     * 创建队列 order.release.order.queue
     *
     * @return
     */
    @Bean
    public Queue orderReleaseOrderQueue() {
        // 队列的名字、是不是持久化的、是不是排他的、是不是自动删除的
        Queue queue = new Queue("order.release.order.queue", true, false, false);
        return queue;
    }

    /**
     * 创建交换机  order.event.exchange
     *
     * @return
     */
    @Bean
    public Exchange orderEventExchange() {
        // 交换机的名字、是不是持久化、是不是自动删除的
        return new TopicExchange("order.event.exchange", true, false);
    }

    /**
     * 将交换机 order.event.exchange 与队列 order.delay.queue 通过路由键 order.create.order 绑定
     *
     * @return
     */
    @Bean
    public Binding orderCreateOrderBinding() {
        // 目的地、目的地的类型、哪个交换机和这个目的地绑定的、路由键
        return new Binding("order.delay.queue", Binding.DestinationType.QUEUE, "order.event.exchange", "order.create.order", null);
    }

    /**
     * 将交换机 order.event.exchange 与队列 order.release.order.queue 通过路由键 order.release.order 绑定
     *
     * @return
     */
    @Bean
    public Binding orderReleaseOrderBinding() {
        // 目的地、目的地的类型、哪个交换机和这个目的地绑定的、路由键
        return new Binding("order.release.order.queue", Binding.DestinationType.QUEUE, "order.event.exchange", "order.release.order", null);
    }


    /**
     * 以下内容应该写在消费者的配置类中
     * 监听 order.release.order.queue 这个队列
     *
     * @param entity  消息的内容
     * @param channel 通道
     * @param message 消息
     * @throws IOException
     */
    @RabbitListener(queues = "order.release.order.queue")
    public void listener(OrderEntity entity, Channel channel, Message message) throws IOException {
        System.out.println("收到过期的订单信息，准备关闭订单。。。" + entity.getOrderSn() + entity.getModifyTime());
        // 手动确认收到消息
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        channel.basicAck(deliveryTag, false);
    }
}
