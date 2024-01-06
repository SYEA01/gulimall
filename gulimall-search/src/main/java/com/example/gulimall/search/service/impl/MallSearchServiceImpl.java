package com.example.gulimall.search.service.impl;

import com.example.gulimall.search.service.MallSearchService;
import com.example.gulimall.search.vo.SearchParam;
import com.example.gulimall.search.vo.SearchResult;
import org.springframework.stereotype.Service;

/**
 * @author taoao
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {
    @Override
    public SearchResult search(SearchParam param) {
        // 1、动态构建出查询需要的DSL语句
        return null;
    }
}
