package com.example.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.common.to.es.SkuEsModel;
import com.example.gulimall.search.config.GulimallElasticSearchConfig;
import com.example.gulimall.search.constant.EsConstant;
import com.example.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author taoao
 */
@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        // 保存到es中
        // 1、给es中建立索引 product，建立好映射关系  【 Kibana操作 建立好了 】
        // 2、给es中保存数据   BulkRequest bulkRequest, RequestOptions options
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel : skuEsModels) {
            // 1、构造保存请求
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);  // 创建indexRequest并指定索引名称
            indexRequest.id(skuEsModel.getSkuId().toString());  // 指定id
            String jsonString = JSON.toJSONString(skuEsModel);
            indexRequest.source(jsonString, XContentType.JSON);  // 把数据转换成JSON字符串，并指定内容类型
            // 2、将保存请求放到批量bulkRequest 中
            bulkRequest.add(indexRequest);
        }

        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);

        // TODO 1、如果批量错误
        boolean b = bulkResponse.hasFailures();
        List<String> collect = Arrays.stream(bulkResponse.getItems()).map(BulkItemResponse::getId).collect(Collectors.toList());
        log.error("商品上架错误：{}", collect);

        return b;
    }
}
