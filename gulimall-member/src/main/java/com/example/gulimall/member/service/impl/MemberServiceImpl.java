package com.example.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.common.utils.HttpUtils;
import com.example.gulimall.member.dao.MemberLevelDao;
import com.example.gulimall.member.entity.MemberLevelEntity;
import com.example.gulimall.member.exception.PhoneExistException;
import com.example.gulimall.member.exception.UsernameExistException;
import com.example.gulimall.member.vo.MemberLoginVo;
import com.example.gulimall.member.vo.MemberRegistVo;
import com.example.gulimall.member.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.gulimall.member.dao.MemberDao;
import com.example.gulimall.member.entity.MemberEntity;
import com.example.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegistVo vo) {
        MemberEntity entity = new MemberEntity();
        // 查询默认会员等级
        MemberLevelEntity levelEntity = memberLevelDao.getDefaultLevel();
        entity.setLevelId(levelEntity.getId());  // 设置默认会员等级

        // 检查用户名和手机号是否唯一.  为了让controller能感知异常，可以使用异常机制
        checkPhoneUnique(vo.getPhone());
        checkUsernameUnique(vo.getUserName());

        entity.setMobile(vo.getPhone());  // 设置手机号
        entity.setUsername(vo.getUserName());  // 设置用户名
        entity.setNickname(vo.getUserName());

        // 密码要进行加密存储
        // MD5不能直接进行密码的加密存储
        // 盐值加密：
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        entity.setPassword(encode);  // 设置密码

        // 其他的默认信息

        // 保存
        baseMapper.insert(entity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (count > 0) {
            // 如果手机号存在，就抛一个异常
            throw new PhoneExistException();
        }
    }

    @Override
    public void checkUsernameUnique(String username) throws UsernameExistException {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if (count > 0) {
            // 如果用户名存在，就抛一个异常
            throw new UsernameExistException();
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();

        // 1、根据用户名或者手机号 去数据库查询
        MemberEntity entity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct).or().eq("mobile", loginacct));
        if (entity == null) {
            // 登录失败
            return null;
        } else {
            // 1、获取到数据库的password
            String passwordDb = entity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            // 2、密码匹配
            boolean matches = passwordEncoder.matches(password, passwordDb);
            if (matches) {
                // 登录成功
                return entity;
            } else {
                return null;
            }
        }
    }

    @Override
    public MemberEntity login(SocialUser vo) throws Exception {
        // 登录和注册合并逻辑
        String uid = vo.getUid();

        // 1、判断当前社交用户是否已经登录过系统（是否注册过）
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if (memberEntity != null) {  // 这个用户已经注册过
            MemberEntity updateMember = new MemberEntity();
            updateMember.setId(memberEntity.getId());
            updateMember.setAccessToken(vo.getAccessToken());
            updateMember.setExpiresIn(vo.getExpiresIn());

            baseMapper.updateById(updateMember);

            memberEntity.setAccessToken(vo.getAccessToken());
            memberEntity.setExpiresIn(vo.getExpiresIn());
            return memberEntity;
        } else {
            // 没有查到当前社交用户对应的记录，就需要注册一个
            MemberEntity registerMember = new MemberEntity();
            try {
                if ("weibo".equals(vo.getIdentification())) {
                    // 查询当前社交用户的社交账号信息（昵称、性别等。。。）
                    Map<String, String> querys = new HashMap<>();
                    querys.put("access_token", vo.getAccessToken());
                    querys.put("uid", vo.getUid());
                    HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<String, String>(), querys);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        // 查询成功
                        String json = EntityUtils.toString(response.getEntity());
                        JSONObject jsonObject = JSON.parseObject(json);
                        // 昵称
                        String nickName = jsonObject.getString("name");
                        // 性别
                        String gender = jsonObject.getString("gender");
                        registerMember.setNickname(nickName);
                        registerMember.setGender("m".equals(gender) ? 1 : 0);
                    }
                } else if ("gitee".equals(vo.getIdentification())) {
                    registerMember.setNickname("gitee_" + UUID.randomUUID().toString().substring(0, 5));
                }
            } catch (Exception ignored) {
            }
            registerMember.setSocialUid(vo.getUid());
            registerMember.setAccessToken(vo.getAccessToken());
            registerMember.setExpiresIn(vo.getExpiresIn());

            baseMapper.insert(registerMember);
            return registerMember;
        }
    }

}