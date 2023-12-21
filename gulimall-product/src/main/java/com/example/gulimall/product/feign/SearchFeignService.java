package com.example.gulimall.product.feign;

import com.example.common.to.es.SkuEsModel;
import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author taoao
 */
@FeignClient("gulimall-search")
public interface SearchFeignService {

    /**
     * 上架商品
     */
    @PostMapping("/search/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
