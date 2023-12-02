package com.example.gulimall.thirdparty;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import com.aliyun.oss.OSSClient;


@SpringBootTest
@RunWith(SpringRunner.class)
public class GulimallThirdPartyApplicationTest {
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
        ossClient.putObject("gulimall-taoao", "hahah.jpg", inputStream);

        ossClient.shutdown();

        System.out.println("上传完成");
    }
}
