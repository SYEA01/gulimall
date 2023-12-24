package com.example.gulimall.product.web;

import com.example.gulimall.product.entity.CategoryEntity;
import com.example.gulimall.product.service.CategoryService;
import com.example.gulimall.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

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

    /**
     * 返回二级分类三级分类的json数据
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        Map<String, List<Catelog2Vo>> map = categoryService.getCatalogJson();
        return map;
    }

}
