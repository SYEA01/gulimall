package com.example.gulimall.seckill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author taoao
 */
@Configuration
@EnableAsync  // 开启异步任务功能
@EnableScheduling  // 开启定时任务功能
public class ScheduledConfig {
}
