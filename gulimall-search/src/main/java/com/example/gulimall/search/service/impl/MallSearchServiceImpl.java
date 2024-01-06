package com.example.gulimall.search.service.impl;

import com.example.gulimall.search.service.MallSearchService;
import com.example.gulimall.search.vo.SearchParam;
import com.example.gulimall.search.vo.SearchResult;
import org.springframework.stereotype.Service;

/**
 * @author taoao
 *
 * # 模糊匹配、过滤（按照属性、分类、品牌、价格区间、库存）、排序、分页、高亮、聚合分析
 * GET product/_search
 * {
 *   "query": {
 *     "bool": {
 *       "must": [
 *         {
 *           "match": {
 *             "skuTitle": "华为"
 *           }
 *         }
 *       ],
 *       "filter": [
 *         {
 *           "term": {
 *             "catalogId": "225"
 *           }
 *         },
 *         {
 *           "terms": {
 *             "brandId": [
 *               "4",
 *               "2",
 *               "9"
 *             ]
 *           }
 *         },
 *         {
 *           "nested": {
 *             "path": "attrs",
 *             "query": {
 *               "bool": {
 *                 "must": [
 *                   {
 *                     "term": {
 *                       "attrs.attrId": "8"
 *                     }
 *                   },
 *                   {
 *                     "terms": {
 *                       "attrs.attrValue": [
 *                         "海思（Hisilicon）",
 *                         "骁龙665"
 *                       ]
 *                     }
 *                   }
 *                 ]
 *               }
 *             }
 *           }
 *         },
 *         {
 *           "term": {
 *             "hasStock": true
 *           }
 *         },
 *         {
 *           "range": {
 *             "skuPrice": {
 *               "gte": 0,
 *               "lte": 6000
 *             }
 *           }
 *         }
 *       ]
 *     }
 *   },
 *   "sort": [
 *     {
 *       "skuPrice": {
 *         "order": "desc"
 *       }
 *     }
 *   ],
 *   "from": 0,
 *   "size": 1,
 *   "highlight": {
 *     "fields": {
 *       "skuTitle": {}
 *     },
 *     "pre_tags": "<b style='color:red'>",
 *     "post_tags": "</b>"
 *   }
 * }
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {
    @Override
    public SearchResult search(SearchParam param) {

        return null;
    }
}
