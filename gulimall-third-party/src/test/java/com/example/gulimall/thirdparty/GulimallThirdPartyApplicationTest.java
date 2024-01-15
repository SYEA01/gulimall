package com.example.gulimall.thirdparty;

import com.example.gulimall.thirdparty.component.SmsComponent;
import com.example.gulimall.thirdparty.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.aliyun.oss.OSSClient;


@SpringBootTest
@RunWith(SpringRunner.class)
public class GulimallThirdPartyApplicationTest {
    @Autowired
    OSSClient ossClient;

    @Autowired
    SmsComponent smsComponent;

    @Test
    public void sendCode() {
        smsComponent.sendSmsCode("18199445947","159159");
    }


    @Test
    public void sendSms() {
        String host = "https://dfsns.market.alicloudapi.com";
        String path = "/data/send_sms";
        String method = "POST";
        String appcode = "96da6c1f643c42e2a654a1ad9b3be38e";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("content", "code:987654");
        bodys.put("template_id", "CST_ptdie100");  //该模板为调试接口专用，短信下发有受限制，调试成功后请联系客服报备专属模板
        bodys.put("phone_number", "18199445947");


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
