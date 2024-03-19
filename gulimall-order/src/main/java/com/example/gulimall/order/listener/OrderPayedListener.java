package com.example.gulimall.order.listener;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 订单支付成功的监听器
 *
 * @author taoao
 */
@RestController
public class OrderPayedListener {

    @PostMapping("/payed/notify")
    public String handleAlipayed(HttpServletRequest request) {
        // 只要我们收到了支付宝给我们的异步通知，告诉我们订单支付成功，我们再给支付宝返回success，支付宝就不再通知了
        Map<String, String[]> map = request.getParameterMap();
        System.out.println("支付宝通知到位了。。。数据：" + map);
        return "success";
    }
}
