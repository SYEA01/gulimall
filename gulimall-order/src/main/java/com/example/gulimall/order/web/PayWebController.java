package com.example.gulimall.order.web;

import com.alipay.api.AlipayApiException;
import com.example.gulimall.order.config.AlipayTemplate;
import com.example.gulimall.order.service.OrderService;
import com.example.gulimall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 支付的Controller
 *
 * @author taoao
 */
@Controller
public class PayWebController {

    @Autowired
    AlipayTemplate alipayTemplate;

    @Autowired
    OrderService orderService;

    /**
     * 1、将支付页让浏览器展示。
     * 2、支付成功以后，我们要跳到用户的订单列表页
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    @GetMapping(value = "/payOrder", produces = "text/html")
    @ResponseBody
    public String payOrder(@RequestParam String orderSn) throws AlipayApiException {
        System.out.println("orderSn = " + orderSn);

        // 获取当前订单的支付信息
        PayVo vo = orderService.getOrderPay(orderSn);
        // 返回的是一个页面，将此页面直接交给浏览器就行
        String pay = alipayTemplate.pay(vo);
        System.out.println("pay = " + pay);
        return pay;
    }
}
