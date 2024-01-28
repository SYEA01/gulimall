package com.example.gulimall.search.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * 指定cookie中session的作用域为 .gulimall.com
 * 指定存到redis中的session的序列化机制为json
 * @author taoao
 */
@Configuration
public class GulimallSessionConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setDomainName("gulimall.com");  // 指定session的域
        cookieSerializer.setCookieName("GULISESSION");  // 还可以指定session的名字
        return cookieSerializer;
    }

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
//        return new GenericFastJsonRedisSerializer();
        return new GenericJackson2JsonRedisSerializer();
    }
}
