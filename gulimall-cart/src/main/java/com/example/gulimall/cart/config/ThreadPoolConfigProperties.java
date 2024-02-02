package com.example.gulimall.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author taoao
 */
@Data
@Component
@ConfigurationProperties(prefix = "gulimall.thread")
public class ThreadPoolConfigProperties {
    private int coreSize;
    private int maxSize;
    private int keepAliveTime;
}
