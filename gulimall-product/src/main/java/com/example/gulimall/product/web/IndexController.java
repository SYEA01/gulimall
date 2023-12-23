package com.example.gulimall.product.web;

import com.example.gulimall.product.entity.CategoryEntity;
import com.example.gulimall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author taoao
 */
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;


    /**
     * 配置不论输入 / 还是 /index.html 都会跳转到首页
     *
     * @return
     */
    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {

        // TODO 查出所有的 1级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categorys();

        // 放到请求域中
        model.addAttribute("categorys", categoryEntities);

        // 默认是转发
        return "index";
    }
}
