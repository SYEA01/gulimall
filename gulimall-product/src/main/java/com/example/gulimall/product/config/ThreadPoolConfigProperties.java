package com.example.gulimall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 可以在application.properties中进行配置线程池的信息
 *
 * @author taoao
 */
@ConfigurationProperties(prefix = "gulimall.thread")
@Component
@Data
public class ThreadPoolConfigProperties {

    /**
     * 核心线程大小
     */
    private Integer coreSize;

    /**
     * 最大线程大小
     */
    private Integer maxSize;

    /**
     * 最大存活时长
     */
    private Integer keepAliveTime;
}
