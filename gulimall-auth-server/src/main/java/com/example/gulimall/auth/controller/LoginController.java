package com.example.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.example.common.constant.AuthServerConstant;
import com.example.common.exception.BizCodeEnume;
import com.example.common.utils.R;
import com.example.gulimall.auth.feign.MemberFeignService;
import com.example.gulimall.auth.feign.ThirdPartFeignService;
import com.example.gulimall.auth.vo.UserRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    MemberFeignService memberFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 获取短信验证码
     *
     * @param phone
     * @return
     */
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

    /**
     * 注册
     * // TODO 重定向携带数据：利用session原理，讲数据放在session中。
     * 只要跳到下一个页面，取出这个数据以后，session里面的数据就会删掉
     * <p>
     * // TODO 1、分布式下的session问题。
     *
     * @param vo
     * @param result
     * @param redirectAttributes 用来模拟重定向视图时，带上数据
     * @return
     */
    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo vo, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {

            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors", errors);  // 重定向携带数据

            // 校验出错，转发到注册页
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        // 1、 校验验证码
        String code = vo.getCode();
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if (!StringUtils.isEmpty(redisCode)) {
            String s = redisCode.split("_")[0];
            if (code.equals(s)) {
                // 验证码通过。   删除验证码；令牌机制
                redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
                // 真正注册，调用远程服务进行注册
                R r = memberFeignService.regist(vo);
                if (r.getCode() == 0) {
                    // 注册成功
                    return "redirect:http://auth.gulimall.com/login.html";
                } else {  // 失败
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg", r.getData(new TypeReference<String>() {
                    }));
                    redirectAttributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.gulimall.com/reg.html";
                }

            } else {
                Map<String, String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.gulimall.com/reg.html";
            }
        } else {  // 验证码过期了
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
    }


}
