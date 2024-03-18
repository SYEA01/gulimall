package com.example.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.example.gulimall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {


    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "9021000135636698";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC8hAf3eZkuVvXtvK+eelVGnt2uOKlBMVnojLBpXwAdYz2+ulPSmD1iAwbRG02y99npcvHrpWVc8ojNeKxqeiBKPtI9SMmX7POD4tfB1i4df1TNpv8l8qMah7nvJhiBeuf+7ZdYHBKadEPIQkanaGe3sCdFzKr5L7jFkxD40zyRAbcINbVYhH0JCzWX9FWO63UbqOaNTGcA2x4OnWqv4Xi+alk7luHVo/Wqdaw7YGOuv8sKJTY4Iz6WQJ6rpSvZlKSa4nyxKHUjR7jzk9cWOpHyj9Tv0J+rQeefRKqOcODdxbLvAfvMYNegFLFHHnm91WLEDKvTFd2R3P/OI/iTOjY9AgMBAAECggEAKuNm5JiXjwFdxg6NbeKrD/wItyBQ8XIG4G9Rm0dvwT/YxlGhzGGyZWAG5v/tN3BH9WKfQ1tHqlPLZ+OyZi8OtZWBQ0wSASx5YmHzcDgIGdHkOz1pAvsmvFxwosXqAVsy+L6ZFgSc/uLAoQjjBicsoY/D/49GvpZq1RhWXiXAu8RhJHtPDLW2E7jExocupqrvG48d3bjg7PNR7iHkzS5CYTnJ0CtcuSb7RRfuV5qCZgSjFnodmx5OFx9zqoQPyzrJZZdBhECY8vawsAFe5M0pbUA7F63EMBnUtxOnR6BouvObNSZ6W05/lHU+PxQ1FY0s/PHRGr+t83V3j92QWDhDQQKBgQD9TGQKHmBy6h+bd/5+Puv+s86cq42AnAElF7Ke7im3dM758gR55aDEN4/Ik4vPpttkYnsDcwfuE8XiFBJ3boZQa3LTDFGQ9t8g5zXMxFwxKEwO0I/Ad3Axo+Of/PerP0NbtgRtKECybFJedwsgzw4xr5lvM0vHecYw7+2Qt96XxQKBgQC+hsHHHEChdtCIuhN9cu2cItxtcWipponX+Uoj6/ip+t0mC1z1aCvGaWZUBaGD/Du9ygST/3Nol2Zk7Jod4vWGatUDcyX9E7mJrrkO/Kv2xwIlPmJsehPEC6ZMNAHOKmbmnF+97hy7AiitU2p2rL0wkA7cq5az3Sw346c0+DoUGQKBgHFZEN9STpi92h0JKlI2OAcHUWrcffrSVNTTcPQb4YEd/UzuF0CxGCsWLfp0xDjNExot05xsilzobkHH96eIRwSPwJdeyNVVE+42aOXT7Ol2feqXO+zFxegAzeiXHAF+0takcgCi5aiyPn3VSZ6J6XEkgkW3r654+M6HX0jGbw59AoGAOAnSvQmmSNd1hoGUcfV3xdDLHLWanyuIp6l/EQm63eQXX7U/5j55gaZGnrf5RaTOvYfJyO39t+mwCEc/HUNADAUoGheoOMOwcrqdW7cdrTvjeajbsiRF5Ae5Jfi1/zmhgnrD/k0mzipYZIKAcd9k1f/JH0ao0MZg1x9ycrX//yECgYBgO9eBrXWypuph0VSZ3eBvYK8lUx4XxqS3jKhhkaoJeuwcRWTfTCiwkS+rFA3vMSN05D8hNiRS3ik1HxvNvanChE2MsbhFS9c4W8QOD2crywldsD2Ma3SwB1j7xlRFesX6smLrA7VdkylNG+jGRzFjOJw5GRg3vM93sL+/ejTrGg==";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhDltOS5skf41rRHuxdEeVT/BjirjYLXKKs9d4MacuA9GW5/R+qRgyIp43XPhOgr+DiZFLS5EQhKMeAVOUFKTVUhke8rbdbE7Rw2mubIpJm6Z12OWz1Q/WbdJWnNDixrrNgrK5ucEK+kROQI/G55vrs3C/4TITHv6qH63ZxSKjlbntOcJz3PSTvtmaotYKBbr3WW4p/rIdl/DTqbUvwCfeQXPbKAD8/cu2n/Io/ATO9Mhw/umxgxa6nIdfWnHtps05wxOF6hiOvh/ajis/Uw1yyLsTK8urs5qAcUnslTncUx86GKp0gNHykGD0CuQBgIzBuAXNv7FlJQYVnTO4gCSmwIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://123.56.95.151:54345/alipay.trade.page.pay-JAVA-UTF-8/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://member.gulimall.com/memberOrder.html";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "E:\\sourceCode\\alipay.trade.page.pay-JAVA-UTF-8\\";


    public String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String outTradeNo = vo.getOutTradeNo();
        //付款金额，必填
        String totalAmount = vo.getTotalAmount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\"" + outTradeNo + "\","
                + "\"total_amount\":\"" + totalAmount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应：" + result);

        return result;

    }
}
