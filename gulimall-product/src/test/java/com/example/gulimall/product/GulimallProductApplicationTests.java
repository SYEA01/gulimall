package com.example.gulimall.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.gulimall.product.entity.BrandEntity;
import com.example.gulimall.product.service.BrandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * 1、引入oss-starter
 * 2、配置Key，endpoint相关信息
 * 3、使用OSSClient进行相关操作
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GulimallProductApplicationTests {
    @Autowired
    private BrandService brandService;

    @Autowired
    OSSClient ossClient;

    @Test
    public void testUpload() throws FileNotFoundException {
//        // Endpoint
//        String endpoint = "oss-cn-beijing.aliyuncs.com";
//        String accessKeyId = "LTAI5tEBjMBdeVSmkzpesNJh";
//        String accessKeySecret = "a0Rx3CNeogfdLh7DxW3vzGfIkJjgDU";
//        // 创建OSSClient实例
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        InputStream inputStream = new FileInputStream("D:\\百度网盘下载路径\\docs\\pics\\335b2c690e43a8f8.jpg");
        ossClient.putObject("gulimall-taoao", "BB.jpg", inputStream);

        ossClient.shutdown();

        System.out.println("上传完成");
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
