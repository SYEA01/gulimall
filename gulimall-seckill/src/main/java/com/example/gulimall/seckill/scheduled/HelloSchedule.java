package com.example.gulimall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务：
 *      1、类上标注 @EnableScheduling  // 开启定时任务功能
 *      2、方法上标注 @Scheduled(cron = "* * * ? * 3")  // 开启一个定时任务
 *      3、自动配置类：TaskSchedulingAutoConfiguration
 *
 * 异步任务：
 *      1、类上标注 @EnableAsync  // 开启异步任务功能
 *      2、方法上标注 @Async  // 异步执行这个方法
 *      3、自动配置类：TaskExecutionAutoConfiguration。 它的属性绑定在：TaskExecutionProperties 类中
 * @author taoao
 */
@EnableScheduling  // 开启定时任务功能
@Component
@Slf4j
@EnableAsync  // 开启异步任务功能
public class HelloSchedule {

    /**
     * 1、Spring中的cron表达式由6位组成，不允许第7位的年
     * 2、Spring中的周几就是数字几 或者使用MON-SUN也可以
     * 3、定时任务不应该阻塞。默认是阻塞的
     *      解决：
     *          方式1）、在定时任务方法内 使用CompletableFuture 异步方式，自己提交到线程池
     *          方式2）、支持定时任务线程池  通过spring.task.scheduling.pool.size来配置线程池的数量
     *          方式3）、让定时任务异步执行，
     *              异步任务：
     *      使用定时任务 + 异步任务来解决定时任务不阻塞的功能
     */
    // 秒 分 时 日 月 周
    @Scheduled(cron = "* * * ? * 3")  // 开启一个定时任务
    @Async  // 异步执行这个方法
    public void hello() throws InterruptedException {
        log.info("hello...");
        Thread.sleep(3000);

    }
}
