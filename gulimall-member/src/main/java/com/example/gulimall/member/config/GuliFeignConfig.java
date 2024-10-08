package com.example.gulimall.member.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author taoao
 */
@Configuration
public class GuliFeignConfig {

    /**
     * 往Spring容器中加入Feign的拦截器，加上浏览器发送请求时的cookie信息
     *
     * @return
     */
    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                // 1、使用RequestContextHolder 拿到刚进来的请求
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (requestAttributes != null) {
                    System.out.println("RequestInterceptor线程---》" + Thread.currentThread().getId());

                    // 老请求
                    HttpServletRequest request = requestAttributes.getRequest();

                    if (request != null) {
                        // 2、同步请求头数据 （同步cookie）
                        String cookie = request.getHeader("Cookie");
                        // 给Feign远程调用时的请求，添加上cookie
                        requestTemplate.header("Cookie", cookie);
                    }
                }
            }
        };
    }
}
