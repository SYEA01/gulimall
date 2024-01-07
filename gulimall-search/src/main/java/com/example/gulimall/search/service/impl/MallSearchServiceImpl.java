package com.example.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.common.to.es.SkuEsModel;
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
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            result = bulidSearchResult(response, param);

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
        if (param.getHasStock() != null) {
            boolQuery.filter(QueryBuilders.termQuery("hasStock", hasStock == 1));
        }
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
                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
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
        // 2.1、排序
        String sort = param.getSort();
        if (!StringUtils.isEmpty(sort)) {
            String[] split = sort.split("_");
            String sortField = split[0];
            String sortRules = split[1];
            sourceBuilder.sort(sortField, "asc".equalsIgnoreCase(sortRules) ? SortOrder.ASC : SortOrder.DESC);
        }

        // 2.2、分页
        Integer pageNum = param.getPageNum();
        sourceBuilder.from((pageNum - 1) * EsConstant.PRODUCT_PAGESIZE);
        sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

        // 2.3、高亮
        if (!StringUtils.isEmpty(keyword)) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }


        /**
         * 聚合分析
         */
        // 品牌聚合
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_agg");
        brandAgg.field("brandId").size(50);
        // 品牌聚合的子聚合
        brandAgg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brandAgg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        sourceBuilder.aggregation(brandAgg);

        // 分类聚合
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalog_agg");
        catalogAgg.field("catalogId").size(20);
        // 分类聚合的子聚合
        catalogAgg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
        sourceBuilder.aggregation(catalogAgg);

        // 属性聚合
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attr_id_agg");
        attrIdAgg.field("attrs.attrId");
        // 聚合分析出当前attrId 对应的attrName
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        // 聚合分析出当前attrId 对应的attrValue
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        attrAgg.subAggregation(attrIdAgg);
        sourceBuilder.aggregation(attrAgg);

        System.out.println("构建的DSL: \n" + sourceBuilder.toString());


        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
        return searchRequest;
    }

    /**
     * 分析响应数据，封装成需要的格式
     *
     * @param response
     * @return
     */
    private SearchResult bulidSearchResult(SearchResponse response, SearchParam param) {

        SearchResult result = new SearchResult();
        // 1、返回的所有查询到的商品
        List<SkuEsModel> esModels = new ArrayList<>();
        SearchHits hits = response.getHits();
        if (hits.getHits() != null && hits.getHits().length > 0) {
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                if (!StringUtils.isEmpty(param.getKeyword())) {
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String string = skuTitle.fragments()[0].string();
                    esModel.setSkuTitle(string);
                }
                esModels.add(esModel);
            }
        }
        result.setProducts(esModels);

        // 2、当前所有商品涉及到的所有属性信息
        ParsedNested attrAgg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attr_id_agg");
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attr_name_agg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);
            List<String> attrValue = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets()
                    .stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
            attrVo.setAttrValue(attrValue);
            attrVos.add(attrVo);
        }
        result.setAttrs(attrVos);

        // 3、当前所有商品涉及到的所有品牌信息
        ParsedLongTerms brandAgg = response.getAggregations().get("brand_agg");
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            brandVo.setBrandId(Long.parseLong(bucket.getKeyAsString()));
            ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brand_img_agg");
            String brandImg = brandImgAgg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brandImg);
            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brand_name_agg");
            String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);
            brandVos.add(brandVo);
        }
        result.setBrands(brandVos);

        // 4、当前所有商品涉及到的所有分类信息
        ParsedLongTerms catalogAgg = response.getAggregations().get("catalog_agg");
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        for (Terms.Bucket bucket : catalogAgg.getBuckets()) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            catalogVo.setCatalogId(Long.parseLong(bucket.getKeyAsString()));
            ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalog_name_agg");
            String catalogName = catalogNameAgg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalogName);
            catalogVos.add(catalogVo);
        }
        result.setCatalogs(catalogVos);

        // 5、分页信息
        result.setPageNum(param.getPageNum());  // 页码
        long total = hits.getTotalHits().value;
        result.setTotal(total);  // 总记录数
        int totalPages = ((int) total % EsConstant.PRODUCT_PAGESIZE == 0) ? ((int) total / EsConstant.PRODUCT_PAGESIZE) : ((int) total / EsConstant.PRODUCT_PAGESIZE + 1);
        result.setTotalPages(totalPages);  // 总页码 - 计算得到

        return result;
    }

}
