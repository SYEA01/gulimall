package com.example.gulimall.product;

import com.example.common.valid.custom.ListValue;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.util.HashSet;
import java.util.Set;

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
 *   5）、自定义校验
 *      步骤：
 *          1、编写一个自定义的校验注解
 *          2、编写一个自定义的校验器
 *          3、关联这个校验器和这个校验注解
 *
 * 4、统一异常处理
 *  1）、对于抛出的异常，可以创建统一异常处理类，
 *      1、使用 @RestControllerAdvice(basePackages="需要异常处理的包") 注解标注这个类
 *      2、使用 @ExceptionHandler(value="具体的异常类型.class") 注解标注其中的方法， 【 返回值类型：R、 参数：具体的异常类型 】
 *  2）、对于常见的异常状态码以及提示消息，可以创建枚举类统一管理
 *
 * 5、VO
 *  作用：
 *      接收页面传递来的数据，封装对象
 *      将业务处理完的对象，封装成页面要用的数据
 *
 * 6、Controller 只接收请求 和 接收和校验数据
 *    Service 接收 Controller 传来的数据，进行业务处理
 *    Controller 接收 Service 处理完的数据，封装成页面指定的Vo
 *
 * 7、To ： 不同服务之间传递的pojo 就是 To
 *
 * 8、feign 使用步骤
 *  步骤：
 *      1、将调用者 与 被调用者 都注册到注册中心中
 *      2、在调用者的启动类中加上 @EnableFeignClients 注解
 *      3、在调用者中创建接口，指定被调用者的完整方法签名 【 @FeignClient("gulimall-coupon")  // 调用哪个远程服务         】
 *                                              【 public interface CouponFeignService {                      】
 *                                              【                                                             】
 *                                              【     @PostMapping("/coupon/spubounds/save")                    】
 *                                              【     R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);       】
 *                                              【 }                                                             】
 *
 * 9、设置服务占用的最大内存  【 VM options   -Xmx100m 】
 *
 * 10、设置批量启动服务
 *  步骤：
 *      1、Edit Configurations...
 *      2、点击 + 号   --->   Compound    --->   依次把服务加进来
 *
 * 11、Spring中日期格式化
 *  在application.yml 中配置： spring.jackson.data-format: yyyy-MM-dd HH:mm:ss
 */
@SpringBootApplication
@MapperScan("com.example.gulimall.product.dao")
@EnableDiscoveryClient  // 开启服务的注册发现功能
@EnableFeignClients(basePackages = "com.example.gulimall.product.feign")  // 开启远程调用。【 basePackages : 告诉feign接口在哪个包下面 】
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
