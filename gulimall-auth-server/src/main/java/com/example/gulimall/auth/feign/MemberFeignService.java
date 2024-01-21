package com.example.gulimall.auth.feign;

import com.example.common.utils.R;
import com.example.gulimall.auth.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 会员服务
 *
 * @author taoao
 */
@FeignClient("gulimall-member")
public interface MemberFeignService {
    /**
     * 远程注册功能
     *
     * @param vo
     * @return
     */
    @PostMapping("/member/member/regist")
    R regist(@RequestBody UserRegistVo vo);
}
