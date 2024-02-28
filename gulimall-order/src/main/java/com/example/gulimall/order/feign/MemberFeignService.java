package com.example.gulimall.order.feign;

import com.example.gulimall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author taoao
 */
@FeignClient("gulimall-member")
public interface MemberFeignService {

    /**
     * 返回会员的所有收货地址列表
     *
     * @param memberId
     * @return
     */
    @GetMapping("/member/memberreceiveaddress/{memberId}/addresses")
    List<MemberAddressVo> getAddress(@PathVariable Long memberId);
}
