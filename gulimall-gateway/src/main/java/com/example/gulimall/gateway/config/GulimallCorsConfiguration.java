package com.example.gulimall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * 配置类 【 解决跨域问题 】
 * @author taoao
 */
@Configuration
public class GulimallCorsConfiguration {

    /**
     *  将 CorsWebFilter 使用 bean 管理，就会起作用
     */
    @Bean
    public CorsWebFilter corsWebFilter(){
        // 跨域的配置信息
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 注册跨域配置
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // 1、配置跨域
        corsConfiguration.addAllowedHeader("*");  // 允许哪些头跨域
        corsConfiguration.addAllowedMethod("*");  // 允许所有请求方式跨域 【GET、POST、PUT、DELETE、...】
        corsConfiguration.addAllowedOrigin("*");  // 允许任意请求来源跨域
        corsConfiguration.setAllowCredentials(true);  // 允许携带cookie跨域

        // 2、给响应

        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsWebFilter(source);
    }
}
