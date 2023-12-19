package com.example.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.example.gulimall.search.config.GulimallElasticSearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallSearchApplicationTests {
    @Autowired
    private RestHighLevelClient client;

    @Test
    public void contextLoads() {

        System.out.println(client);
    }

    /**
     * 测试给ES中存储数据
     */
    @Test
    public void indexData() throws IOException {
        // 步骤1：构造IndexRequest
        IndexRequest indexRequest = new IndexRequest("users");  // 参数是索引名称
        indexRequest.id("1");  // 指定id

//        // 保存数据的方式1
//        indexRequest.source("userName", "男", "age", 18, "gender", "男");

        // 保存数据的第二种方式
        User user = new User();
        user.setUsername("张三");
        user.setAge(23);
        user.setGender("男");
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);

        // 步骤2：发送保存请求
        IndexResponse indexResponse = client.index(indexRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);

        System.out.println("indexResponse = " + indexResponse);

    }

    @Data
    class User {
        private String username;
        private String gender;
        private int age;
    }
}
