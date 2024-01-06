package com.example.gulimall.search.service;

import com.example.gulimall.search.vo.SearchParam;

/**
 * @author taoao
 */
public interface MallSearchService {
    /**
     * @param param 检索的所有参数
     * @return 返回检索的结果
     */
    Object search(SearchParam param);
}
