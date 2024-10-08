package com.example.gulimall.auth.feign;

import com.example.common.utils.R;
import com.example.gulimall.auth.vo.SocialUser;
import com.example.gulimall.auth.vo.UserLoginVo;
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

    /**
     * 远程登录功能
     *
     * @param vo
     * @return
     */
    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo vo);

    /**
     * 远程社交登录
     *
     * @param vo
     * @return
     * @throws Exception
     */
    @PostMapping("/member/member/oauth2/login")
    R oauthLogin(@RequestBody SocialUser vo) throws Exception;
}
