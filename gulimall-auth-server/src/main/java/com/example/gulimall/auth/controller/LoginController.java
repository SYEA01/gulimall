package com.example.gulimall.auth.controller;

import com.example.common.utils.R;
import com.example.gulimall.auth.feign.ThirdPartFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

/**
 * @author taoao
 */
@Controller
public class LoginController {
    /**
     * 发送一个请求直接跳转到一个页面，什么操作也不做
     * 这种情况可以使用SpringMVC的viewController功能：将请求和页面进行映射
     */

    @Autowired
    ThirdPartFeignService thirdPartFeignService;


    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone) {
        String code = UUID.randomUUID().toString().substring(0, 5);

        thirdPartFeignService.sendCode(phone, code);
        return R.ok();
    }

}
