package com.example.gulimall.member.web;

import com.alibaba.fastjson.JSON;
import com.example.common.utils.R;
import com.example.gulimall.member.feign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * @author taoao
 */
@Controller
public class MemberWebController {

    @Autowired
    OrderFeignService orderFeignService;


    @GetMapping("/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  Model model) {
        // 可以获取到支付宝传来的所有请求数据
        // request.验证签名，如果正确就可以修改订单状态

        Map<String, Object> params = new HashMap<>();
        params.put("page", pageNum.toString());
        R r = orderFeignService.listWithItem(params);
        System.out.println("---------------" + JSON.toJSONString(r));
        model.addAttribute("orders", r);

        // 查出当前登录的用户的所有订单列表数据
        return "orderList";
    }

}
