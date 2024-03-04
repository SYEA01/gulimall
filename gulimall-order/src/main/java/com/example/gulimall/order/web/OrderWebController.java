package com.example.gulimall.order.web;

import com.example.gulimall.order.service.OrderService;
import com.example.gulimall.order.vo.OrderConfirmVo;
import com.example.gulimall.order.vo.OrderSubmitVo;
import com.example.gulimall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.concurrent.ExecutionException;

/**
 * @author taoao
 */
@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        // 订单确认页 返回需要的数据
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData", confirmVo);
        // 展示订单确认的数据
        return "confirm";
    }

    /**
     * 下单功能
     *
     * @param vo
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo) {
        // 下单去创建订单，验令牌，验价格，锁库存。。。
        SubmitOrderResponseVo responseVo = orderService.submitOrder(vo);

        System.out.println("订单提交的数据。。。" + vo);
        if (responseVo.getCode() == 0) {
            // 下单成功跳转到支付选择页
            return "pay";
        } else {
            // 下单失败回到订单确认页重新确认订单信息
            return "redirect:http://order.gulimall.com/toTrade";
        }
    }
}
