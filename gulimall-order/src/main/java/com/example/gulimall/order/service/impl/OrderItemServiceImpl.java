package com.example.gulimall.order.service.impl;

import com.example.gulimall.order.entity.OrderEntity;
import com.example.gulimall.order.entity.OrderReturnReasonEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.gulimall.order.dao.OrderItemDao;
import com.example.gulimall.order.entity.OrderItemEntity;
import com.example.gulimall.order.service.OrderItemService;

/**
 * @author taoao
 */
@RabbitListener(queues = {"hello.java.queue"})
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * @RabbitListener 监听队列，接收消息
     * ---------注解的参数： queues 声明需要监听的队列，是个数组
     * --、方法接收消息时的参数  可以写以下几种类型
     * -------方法0、Object obj   【 可以直接写成Object类型， 】
     * -------方法1、Message message  【 原生消息详细信息，包含消息头、消息体 】
     * -------方法2、OrderReturnReasonEntity 【 可以直接写发送消息的类型 】
     * -------方法3、Channel channel  【 当前传输数据的通道 】
     * <p>
     * --、 队列可以有很多人来监听。只要有一个人收到此消息，队列就会删除这条消息 【 只能有一个人接收 】
     * --、 只有当前接收消息的方法处理完成之后，才能再接收下一条消息
     */

    /**
     * 在接收消息时使用channl接收消息，手动确认收到消息
     * 只要不手动确认收到消息，消息就一直在queue中存在，即使Java服务器宕机，消息也不会丢失
     *
     * @param message
     * @param content
     * @param channel
     */
//    @RabbitListener(queues = {"hello.java.queue"})
    @RabbitHandler
    public void receiveMessage(Message message,
                               OrderReturnReasonEntity content,
                               Channel channel) {
//        // 拿到消息体
//        byte[] body = message.getBody();
//
//        // 消息头的属性信息
//        MessageProperties messageProperties = message.getMessageProperties();
//
//        System.out.println("参数的第一种写法：原生消息：" + message);
//        System.out.println("参数的第二种写法：直接写消息的类型：" + content);
//        System.out.println("参数的第三种写法：接收消息的通道：" + channel);

        // 消息的标识，是在channel内自增的
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            if (deliveryTag % 2 == 0) {
                // 确认接收消息
                // 两个参数：第一个：消息的标识    第二个：是否批量确认（channel有很多消息，true代表批量确认；false代表单个确认）
                channel.basicAck(deliveryTag, false);  // 确认收到消息
                System.out.println("收到了消息：" + deliveryTag);
            } else {
                // 确认不接收消息 (方式1)
                // 三个参数：消息的标识  是否批量拒绝  是否重新放入队列
                channel.basicNack(deliveryTag, false, true);
                // 确认不接收消息 (方式2)
                // 两个参数：消息的标识   是否重新放入队列
                // channel.basicReject();
                System.out.println("没有收到货物：" + deliveryTag);
            }
        } catch (IOException e) {
            // 网络中断了
        }
    }

    /**
     * 如果这个队列接收多种类型的消息，可以将@RabbitListener注解标注在类上，代表监听哪个队列
     * 然后在不同的方法上使用@RabbitHandler注解，分别接收不同类型的消息
     *
     * @param entity
     */
//    @RabbitHandler
    public void receiveMessage1(OrderReturnReasonEntity entity) {
        System.out.println("接收到的第一种消息：" + entity);
    }

//    @RabbitHandler
    public void receiveMessage2(OrderEntity entity) {
        System.out.println("接收到的第二种消息：" + entity);
    }


}