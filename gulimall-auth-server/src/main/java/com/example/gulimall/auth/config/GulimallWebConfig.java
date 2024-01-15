package com.example.gulimall.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author taoao
 */
@Configuration
public class GulimallWebConfig implements WebMvcConfigurer {

    /**
     * 添加视图控制器，无需写Controller逻辑直接渲染一个页面
     * 视图映射
     *
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login.html")  // 添加视图控制器：@GetMapping中的url
                .setViewName("login");  // 逻辑视图的名字：Controller方法的返回值

        registry.addViewController("/reg.html")
                .setViewName("reg");
    }
}
