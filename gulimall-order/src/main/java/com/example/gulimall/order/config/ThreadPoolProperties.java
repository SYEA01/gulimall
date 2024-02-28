package com.example.gulimall.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author taoao
 */
@Data
@Component
@ConfigurationProperties("gulimall.thread")
public class ThreadPoolProperties {
    /**
     * 核心连接数
     */
    private Integer coreSize;
    /**
     * 最大连接数
     */
    private Integer maxSize;
    /**
     * 最大存活时间
     */
    private Integer keepAliveTime;
}
