package com.example.gulimall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author taoao
 * 整合 elasticsearch-rest-high-level-client 步骤
 * 1、导入依赖
 * 2、编写配置：给容器中注入一个RestHighLevelClient
 */
@Configuration
public class GulimallElasticSearchConfig {

    /**
     * 通用设置项
     */
    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();

        COMMON_OPTIONS = builder.build();
    }

    @Bean
    public RestHighLevelClient esRestClient() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(  // 它里面可以指定多个HttpHost
                        new HttpHost("192.168.100.135", 9200, "http")
                )
        );
        return client;
    }
}
