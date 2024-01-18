package com.example.gulimall.auth.controller;

import com.example.common.constant.AuthServerConstant;
import com.example.common.exception.BizCodeEnume;
import com.example.common.utils.R;
import com.example.gulimall.auth.feign.ThirdPartFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    StringRedisTemplate redisTemplate;

    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone) {
        // TODO 1、接口防刷

        // 如果小于60秒，60秒内不能再发
        // 先取出reids中存取的验证码的时间，再与当前系统时间进行比较
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)) {
            long l = Long.parseLong(redisCode.split("_")[1]);
            if ((System.currentTimeMillis() - l) < 60 * 1000) {
                return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(), BizCodeEnume.SMS_CODE_EXCEPTION.getMessage());
            }
        }


//        String code = UUID.randomUUID().toString().substring(0, 5);
        String code = "123123";


        // 2、验证码的再次校验。redis  存 key-phone   value-code   过期时间10分钟
        // reids存验证码，防止同一个手机号在60秒内再次发送验证码、
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, code + "_" + System.currentTimeMillis(), 10, TimeUnit.MINUTES);

        thirdPartFeignService.sendCode(phone, code);
        return R.ok();
    }


}
