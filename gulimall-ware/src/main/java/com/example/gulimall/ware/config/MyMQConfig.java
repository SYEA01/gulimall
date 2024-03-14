package com.example.gulimall.ware.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author taoao
 */
@Configuration
public class MyMQConfig {

    /**
     * 创建一个交换机stock.event.exchange
     *
     * @return
     */
    @Bean
    public Exchange stockEventExchange() {
        return new TopicExchange("stock.event.exchange", true, false);
    }

    /**
     * 创建一个普通队列stock.release.stock.queue
     * @return
     */
    @Bean
    public Queue stockReleaseStockQueue() {
        return new Queue("stock.release.stock.queue", true, false, false);
    }

    /**
     * 创建死信队列stock.delay.queue
     *
     * @return
     */
    @Bean
    public Queue stockDelayQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "stock.event.exchange");  // 配置死信交换机
        arguments.put("x-dead-letter-routing-key", "stock.release");  // 配置死信路由
        arguments.put("x-message-ttl", 1000 * 60 * 2);  // 配置消息过期时间
        return new Queue("stock.delay.queue", true, false, false, arguments);
    }

    /**
     * 将交换机stock.event.exchange 与队列stock.release.stock.queue 通过路由键stock.release.# 进行绑定
     * @return
     */
    @Bean
    public Binding stockReleaseBinding() {
        return new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE, "stock.event.exchange", "stock.release.#", null);
    }

    /**
     * 将交换机stock.event.exchange 与队列stock.delay.queue 通过路由键stock.locked 进行绑定
     * @return
     */
    @Bean
    public Binding stockLockedBinding() {
        return new Binding("stock.delay.queue", Binding.DestinationType.QUEUE, "stock.event.exchange", "stock.locked", null);
    }

//    /**
//     * 随便监听一个队列，当服务第一次连接MQ的时候，会自动创建出@Bean配置了，但是MQ中没有的队列、交换机、绑定关系
//     * @param message
//     */
//    @RabbitListener(queues = "stock.release.stock.queue")
//    public void handle(Message message){
//
//    }


}
