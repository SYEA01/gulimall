package com.example.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 1、整合MyBatis-Plus
 *  1）、导入依赖
 *  <dependency>
 *      <groupId>com.baomidou</groupId>
 *      <artifactId>mybatis-plus-boot-starter</artifactId>
 *      <version>3.2.0</version>
 *  </dependency>
 *  2）、配置
 *      1、配置数据源；
 *          1）、导入数据库的驱动
 *          2）、在application.yml配置数据源的相关信息
 *      2、配置MyBatis-Plus；
 *          1）、使用@MapperScan
 *          2）、告诉MyBatis-Plus，sql映射文件位置
 *
 * 2、逻辑删除
 *  步骤 1: 配置com.baomidou.mybatisplus.core.config.GlobalConfig$DbConfig
 *      mybatis-plus:
 *          global-config:
 *              db-config:
 *                  logic-delete-field: flag # 全局逻辑删除的实体字段名(since 3.3.0,配置后可以忽略不配置步骤2)
 *                  logic-delete-value: 1 # 逻辑已删除值(默认为 1)
 *                  logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)
 *  步骤 2: 实体类字段上加上@TableLogic注解
 *      @TableLogic
 *      private Integer deleted;
 */
@SpringBootApplication
@MapperScan("com.example.gulimall.product.dao")
@EnableDiscoveryClient  // 开启服务的注册发现功能
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
