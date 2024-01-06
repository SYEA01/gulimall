package com.example.gulimall.search.controller;

import com.example.gulimall.search.service.MallSearchService;
import com.example.gulimall.search.vo.SearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author taoao
 */
@Controller
public class SearchController {

    @Autowired
    MallSearchService mallSearchService;

    /**
     * 自动将页面提交过来的所有请求查询参数封装成指定的对象
     * @param param 页面提交过来的所有请求查询参数
     * @return
     */
    @GetMapping("/list.html")
    public String listPage(SearchParam param) {
        Object result = mallSearchService.search(param);
amend
        return "list";
    }
}
