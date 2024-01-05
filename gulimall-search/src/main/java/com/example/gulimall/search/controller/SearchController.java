package com.example.gulimall.search.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author taoao
 */
@Controller
public class SearchController {

    @GetMapping("/list.html")
    public String listPage() {
        return "list";
    }
}
