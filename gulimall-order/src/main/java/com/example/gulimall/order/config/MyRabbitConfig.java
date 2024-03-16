package com.example.gulimall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author taoao
 */
@Configuration
public class MyRabbitConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 定制RabbitTemplate  【 设置确认回调 】
     * 1、服务器收到消息就回调
     */
    @PostConstruct  // 在MyRabbitConfig这个配置类的构造器创建完（MyRabbitConfig对象创建完）之后，调用这个方法
    public void initRabbitTemplate() {
        // 设置确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 当交换机收到消息，就会自动回调这个方法
             * 1、只要消息抵达Broker，ack就是true
             * @param correlationData correlation data for the callback. 当前消息的唯一关联数据【 唯一id 】
             * @param ack true for ack, false for nack 消息是否成功收到
             * @param cause An optional cause, for nack, when available, otherwise null. 失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                // 服务器收到了
                // 修改消息的状态
                System.out.println("confirm...correlationData[" + correlationData + "]===>ack[" + ack + "]===>cause[" + cause  + "]");
            }

        });

        /**
         * 设置消息抵达队列的确认回调
         * 1、只要消息抵达queue
         */
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 失败回调 【 消息抵达queue不会触发 】
             * 只要消息没有抵达queue，就触发这个失败回调
             * @param message the returned message. 哪个消息投递到queue失败，消息的详细信息
             * @param replyCode the reply code.  回复的状态码
             * @param replyText the reply text. 回复的文本内容
             * @param exchange the exchange. 当时这个消息发给哪个交换机
             * @param routingKey the routing key. 当时这个消息用的哪个路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                // 报错误了，修改数据库当前消息的状态-》错误。
                System.out.println("Fail Message[" + message + "]==>replyCode[" + replyCode + "]===>replyText[" + replyText + "]===>exchange[" + exchange + "]==>routingKey[" + routingKey + "]");
            }
        });
    }


    /**
     * 使用JSON序列化机制，进行消息转换
     *
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


}
