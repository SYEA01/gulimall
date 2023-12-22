package com.example.gulimall.search.controller;

import com.example.common.exception.BizCodeEnume;
import com.example.common.to.es.SkuEsModel;
import com.example.common.utils.R;
import com.example.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author taoao
 */
@Slf4j
@RestController
@RequestMapping("/search")
public class ElasticSaveController {

    @Autowired
    ProductSaveService productSaveService;

    /**
     * 上架商品
     */
    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {
        boolean bl = false;
        try {
            bl = productSaveService.productStatusUp(skuEsModels);
        } catch (Exception e) {
            // 如果失败
            log.error("ElasticSaveController商品上架错误：{}", e);
            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnume.PRODUCT_UP_EXCEPTION.getMessage());
        }

        if (!bl) {
            return R.ok();
        } else {
            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnume.PRODUCT_UP_EXCEPTION.getMessage());
        }
    }
}
