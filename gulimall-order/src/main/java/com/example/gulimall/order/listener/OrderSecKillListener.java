package com.example.gulimall.order.listener;

import com.example.common.to.mq.SecKillOrderTo;
import com.example.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 监听秒杀消息
 */
@Slf4j
@Service
@RabbitListener(queues = "order.seckill.order.queue")
public class OrderSecKillListener {

    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void listener(SecKillOrderTo to, Channel channel, Message message) throws IOException {
        try {
            log.info("准备创建秒杀订单的详细信息。。。");
            // 创建秒杀订单
            orderService.createSecKillOrder(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }

    }

}
