package com.example.gulimall.order.listener;

import com.example.gulimall.order.entity.OrderEntity;
import com.example.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 延时队列 定时关单业务
 *
 * @author taoao
 */
@Service
@RabbitListener(queues = "order.release.order.queue")
public class OrderCloseListener {

    @Autowired
    OrderService orderService;

    /**
     * 以下内容应该写在消费者的配置类中
     * 监听 order.release.order.queue 这个队列
     *
     * @param entity  消息的内容
     * @param channel 通道
     * @param message 消息
     * @throws IOException
     */
    @RabbitHandler
    public void listener(OrderEntity entity, Channel channel, Message message) throws IOException {
        System.out.println("收到过期的订单信息，准备关闭订单。。。" + entity.getOrderSn() + entity.getModifyTime());
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            orderService.closeOrder(entity);
            // 一切正常 ， 手动确认收到消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            // 拒绝接收消息，并将这条消息放回到队列中
            channel.basicReject(deliveryTag, true);
        }


    }

}
