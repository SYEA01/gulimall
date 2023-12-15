package com.example.gulimall.ware.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author taoao
 * MyBatis Plus配置分页插件
 */
@Configuration
@MapperScan("com.example.gulimall.ware.dao")  // 开启MyBatis注解扫描
@EnableTransactionManagement  // 开启事务功能
public class WareMyBatisConfig {

    // 引入分页插件
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
//        // 设置请求的页面大于最后页 ，true调回首页，false继续请求，默认false
//        paginationInterceptor.setOverflow(true);
//
//        // 设置最大单页限制数量 默认500条  -1代表不受限制
//        paginationInterceptor.setLimit(1000);

        return paginationInterceptor;
    }

}
