package com.example.gulimall.product;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.gulimall.product.dao.AttrGroupDao;
import com.example.gulimall.product.dao.SkuSaleAttrValueDao;
import com.example.gulimall.product.entity.BrandEntity;
import com.example.gulimall.product.service.BrandService;
import com.example.gulimall.product.service.CategoryService;
import com.example.gulimall.product.vo.SkuItemSaleAttrVo;
import com.example.gulimall.product.vo.SkuItemVo;
import com.example.gulimall.product.vo.SpuItemAttrGroupVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 1、引入oss-starter
 * 2、配置Key，endpoint相关信息
 * 3、使用OSSClient进行相关操作
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GulimallProductApplicationTests {
    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 自动注入StringRedisTemplate，它的key和value都是String类型的
     */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Test
    public void test2(){
        List<SkuItemSaleAttrVo> saleAttrsBySpuId = skuSaleAttrValueDao.getSaleAttrsBySpuId(3L);
        System.out.println("saleAttrsBySpuId = " + saleAttrsBySpuId);
    }

    @Test
    public void test(){
        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(993L, 225L);
        System.out.println("attrGroupWithAttrsBySpuId = " + attrGroupWithAttrsBySpuId);
    }


    @Test
    public void redisson(){
        System.out.println("redissonClient = " + redissonClient);
    }

    @Test
    public void testStringRedisTemplate() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();

        // 保存
        ops.set("hello", "world_" + UUID.randomUUID().toString());

        // 查询
        String value = ops.get("hello");
        System.out.println("value = " + value);
    }


    @Test
    public void testFindPath() {
        Long[] catelogPath = categoryService.findCatelogPath(225L);
        log.info("完整路径: {}", Arrays.asList(catelogPath));
    }


    @Test
    public void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setName("华为");
//        brandService.save(brandEntity);
//        System.out.println("保存成功");

//        brandEntity.setBrandId(1L);
//        brandEntity.setDescript("华为");
//        brandService.updateById(brandEntity);
//        System.out.println("保存成功");

        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        list.forEach(brand -> System.out.println(brand));

    }

}
