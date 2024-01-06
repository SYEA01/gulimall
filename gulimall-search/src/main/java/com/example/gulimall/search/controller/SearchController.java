package com.example.gulimall.search.controller;

import com.example.gulimall.search.service.MallSearchService;
import com.example.gulimall.search.vo.SearchParam;
import com.example.gulimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
     *
     * @param param 页面提交过来的所有请求查询参数
     * @return
     */
    @GetMapping("/list.html")
    public String listPage(SearchParam param, Model model) {
        // 1、根据传递来的页面查询参数，去ES中检索商品
        SearchResult result = mallSearchService.search(param);
        // 2、把查询到的结果传递给前端
        model.addAttribute("result", result);

        return "list";
    }
}
