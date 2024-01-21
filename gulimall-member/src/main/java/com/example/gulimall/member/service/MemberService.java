package com.example.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.gulimall.member.entity.MemberEntity;
import com.example.gulimall.member.exception.PhoneExistException;
import com.example.gulimall.member.exception.UsernameExistException;
import com.example.gulimall.member.vo.MemberLoginVo;
import com.example.gulimall.member.vo.MemberRegistVo;

import java.util.Map;

/**
 * 会员
 *
 * @author tpc
 * @email tpc@gmail.com
 * @date 2023-08-02 17:21:42
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 注册
     *
     * @param vo
     */
    void regist(MemberRegistVo vo);

    /**
     * 检查手机号是否唯一
     *
     * @param phone
     * @return
     */
    void checkPhoneUnique(String phone) throws PhoneExistException;

    /**
     * 检查手机号是否唯一
     *
     * @param username
     * @return
     */
    void checkUsernameUnique(String username) throws UsernameExistException;

    /**
     * 登录
     * @param vo
     * @return
     */
    MemberEntity login(MemberLoginVo vo);
}

