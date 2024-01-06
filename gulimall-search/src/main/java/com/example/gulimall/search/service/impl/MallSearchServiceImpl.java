package com.example.gulimall.search.service.impl;

import com.example.gulimall.search.config.GulimallElasticSearchConfig;
import com.example.gulimall.search.constant.EsConstant;
import com.example.gulimall.search.service.MallSearchService;
import com.example.gulimall.search.vo.SearchParam;
import com.example.gulimall.search.vo.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author taoao
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient client;

    // 去ES中进行检索
    @Override
    public SearchResult search(SearchParam param) {
        // 1、动态构建出查询需要的DSL语句
        SearchResult result = null;

        // 1、准备检索请求
        SearchRequest searchRequest = buildSearchRequest(param);
        try {
            // 2、执行检索请求
            SearchResponse response = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
            // 3、分析响应数据，封装成需要的格式
            result = bulidSearchResult(response);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return result;
    }

    /**
     * 准备检索请求
     * 模糊匹配、过滤（按照属性、分类、品牌、价格区间、库存）、排序、分页、高亮、聚合分析
     *
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();  // 构建DSL语句
        /**
         * 查询： 模糊匹配，过滤（按照属性、分类、品牌、价格区间、库存）
         */
        // 1、构建boolQuery
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 1.1、 must模糊匹配
        String keyword = param.getKeyword();
        if (!StringUtils.isEmpty(keyword)) {
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", keyword));
        }
        // 1.2、filter过滤 - 按照三级分类id查询
        Long catalog3Id = param.getCatalog3Id();
        if (catalog3Id != null) {
            boolQuery.filter(QueryBuilders.termQuery("catalog3Id", catalog3Id));
        }
        // 1.2、filter过滤 - 按照品牌id查询
        List<Long> brandIds = param.getBrandId();
        if (brandIds != null && brandIds.size() > 0) {
            boolQuery.filter(QueryBuilders.termQuery("brandId", brandIds));
        }
        // 1.2、filter过滤 - 按照是否拥有库存查询
        Integer hasStock = param.getHasStock();
        boolQuery.filter(QueryBuilders.termQuery("hasStock", hasStock == 1));
        // 1.2、filter过滤 - 按照价格区间查询
        String skuPrice = param.getSkuPrice();
        if (!StringUtils.isEmpty(skuPrice)) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] s = skuPrice.split("_");
            if (s.length == 2) {
                rangeQuery.gte(s[0]).lte(s[1]);
            } else if (s.length == 1) {
                if (skuPrice.startsWith("_")) {
                    rangeQuery.lte(s[0]);
                } else if (skuPrice.endsWith("_")) {
                    rangeQuery.gte(s[0]);
                }
            }
            boolQuery.filter(rangeQuery);
        }
        // 1.2、filter过滤 - 按照所有指定的属性查询
        List<String> attrs = param.getAttrs();
        if (attrs != null && attrs.size() > 0) {
            for (String attr : attrs) {
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                String[] split = attr.split("_");
                String attrId = split[0];  // 检索的属性id
                String[] attrValues = split[1].split(":");  // 属性的值
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrValue", attrValues));
                // 每一个NestedQueryBuilder 都必须放到最大的boolQueryBuilder中
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);  // ScoreMode.None代表这个查询的结果不参与评分
                boolQuery.filter(nestedQuery);
            }
        }

        // 把以前的所有query条件都拿来
        sourceBuilder.query(boolQuery);

        /**
         * 排序、分页、高亮
         */

        /**
         * 聚合分析
         */

        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
        return searchRequest;
    }

    /**
     * 分析响应数据，封装成需要的格式
     *
     * @param response
     * @return
     */
    private SearchResult bulidSearchResult(SearchResponse response) {

        return null;
    }

}
