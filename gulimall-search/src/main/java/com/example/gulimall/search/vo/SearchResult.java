package com.example.gulimall.search.vo;

import com.example.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * @author taoao
 */
@Data
public class SearchResult {

    private List<SkuEsModel> products;  // 从ES中查询到的所有商品信息

    /**
     * 分页信息
     */
    private Integer pageNum;  // 当前页码
    private Long total;  // 总记录数
    private Integer totalPages;  // 总页码
    private List<Integer> pageNavs;  // 导航页码

    private List<BrandVo> brands;  // 当前查询到的结果，所有涉及到的品牌
    private List<CatalogVo> catalogs; // 当前查询到的结果，所有涉及到的分类
    private List<AttrVo> attrs;  // 当前查询到的结果，所有涉及到的属性

    //===========================以上是返回给页面的所有信息==============================


    /**
     * 品牌信息
     */
    @Data
    public static class BrandVo {
        private Long brandId;  //  品牌id
        private String brandName;  //  品牌名字
        private String brandImg;  // 品牌图片
    }

    /**
     * 分类信息
     */
    @Data
    public static class CatalogVo {
        private Long catalogId;  // 分类id
        private String catalogName;  // 分类的名字
    }

    /**
     * 属性信息
     */
    @Data
    public static class AttrVo {
        private Long attrId;  // 属性id
        private String attrName;  // 属性名字

        private List<String> attrValue;  // 属性值
    }

}
