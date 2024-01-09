package com.example.gulimall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author taoao
 */
@EnableDiscoveryClient  // 启用服务的注册发现
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)  // 如果没有配置数据源相关的配置，但是导入了MyBatis之类的依赖，就需要在主方法这里排除掉
@EnableFeignClients  // 开启远程调用
public class GulimallSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallSearchApplication.class, args);
    }

}
