package com.example.gulimall.search.service;

import com.example.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author taoao
 */
public interface ProductSaveService {

    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
