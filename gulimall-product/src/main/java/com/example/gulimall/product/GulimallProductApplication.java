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
 *
 * 3、JSR303数据校验
 *  1）、给Bean中的字段添加校验注解，并定义自己的message 提示  【 javax.validation.constraints 包下 】
 *  2）、开启校验功能 ： 在Controller接口接收参数时，使用 @Valid 注解
 *      效果：校验错误以后会有默认的响应
 *  3）、给校验的bean后紧跟一个BindingResult，就可以获取到校验的结果  【 public R save(@Valid @RequestBody BrandEntity brand, BindingResult result){} 】
 *
 *  4）、分组检验（多场景的复杂校验）
 *      步骤：
 *          1、首先创建好多个空接口，用作不同的分组
 *          2、给校验注解标注 groups 字段，指定 ”步骤1“中创建的空接口
 *          3、Controller 中，接口上使用【 @Validated(分组（空接口）.class) 】注解  【【      public R save(@Validated(AddGroup.class) @RequestBody BrandEntity brand) {}  】】
 *      注意：
 *          1、@Validated 注解如果不指定分组，那么都会生效
 *          2、@Validated(value={分组.class}) 注解如果指定了分组，那么它只会校验对应分组的数据是否正常
 *
 * 4、统一异常处理
 *  1）、对于抛出的异常，可以创建统一异常处理类，
 *      1、使用 @RestControllerAdvice(basePackages="需要异常处理的包") 注解标注这个类
 *      2、使用 @ExceptionHandler(value="具体的异常类型.class") 注解标注其中的方法， 【 返回值类型：R、 参数：具体的异常类型 】
 *  2）、对于常见的异常状态码以及提示消息，可以创建枚举类统一管理
 *
 */
@SpringBootApplication
@MapperScan("com.example.gulimall.product.dao")
@EnableDiscoveryClient  // 开启服务的注册发现功能
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
