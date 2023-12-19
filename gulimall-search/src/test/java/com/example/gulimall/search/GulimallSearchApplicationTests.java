package com.example.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.example.gulimall.search.config.GulimallElasticSearchConfig;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;

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

/**
 * 1、方便检索 【优秀】
 * {
 *      // sku的基本信息
 *     skuId:1
 *     spuId:11
 *     skuTitle:华为xxx
 *     price:998
 *     saleCount:99
 *
 *      // 检索属性
 *     attrs:[
 *          {尺寸: 5寸},
 *          {CPU: 高通945},
 *          {分辨率: 全高清}
 *     ]
 * }
 * 2、
 * sku索引 {
 *     skuId:1
 *     spuId:11
 *     xxx
 * }
 * attrs索引{
 *     spuId:11
 *     attrs:[
 *          {尺寸: 5寸},
 *          {CPU: 高通945},
 *          {分辨率: 全高清}
 *     ]
 * }
 */
    /**
     * 测试检索功能
     */
    @Test
    public void searchData() throws IOException {
        // 1、创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");  // 指定索引
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 1.1、构造检索条件
        sourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));  // 查询address中包含mill的
        sourceBuilder.aggregation(AggregationBuilders.terms("ageAgg").field("age").size(10));  // 聚合：查询年龄分布
        sourceBuilder.aggregation(AggregationBuilders.avg("balanceAvg").field("balance"));  // 聚合：查询平均工资
        System.out.println("检索条件DSL = " + sourceBuilder);

        searchRequest.source(sourceBuilder); // 指定DSL（检索条件）

        // 2、执行检索
        SearchResponse searchResponse = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);

        // 3、分析结果  searchResponse
        System.out.println("结果 = " + searchResponse);
//        Map map = JSON.parseObject(searchResponse.toString(), Map.class);
        // 3.1、获取所有查到的数据
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            String hitSourceAsString = hit.getSourceAsString();
            Account account = JSON.parseObject(hitSourceAsString, Account.class);
            System.out.println("account = " + account);
        }
        // 3.2、获取这次检索到的聚合信息
        Aggregations aggregations = searchResponse.getAggregations();
        Terms ageAgg = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            System.out.println("keyAsString = " + keyAsString + "==>" +bucket.getDocCount());
        }

        Avg balanceAvg = aggregations.get("balanceAvg");
        System.out.println("balanceAvg.getValue() = " + balanceAvg.getValue() + "==>");

    }

    @Data
    @ToString
    static class Account {

        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
    }

}
