package com.example.gulimall.product.service.impl;

import com.example.gulimall.product.entity.AttrEntity;
import com.example.gulimall.product.service.AttrService;
import com.example.gulimall.product.vo.AttrGroupWithAttrsVo;
import com.example.gulimall.product.vo.SkuItemVo;
import com.example.gulimall.product.vo.SpuItemAttrGroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.gulimall.product.dao.AttrGroupDao;
import com.example.gulimall.product.entity.AttrGroupEntity;
import com.example.gulimall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        String key = (String) params.get("key");
        // select * from pms_attr_group where cateLog_id=? and (attr_group_id=key or attr_group_name like %key%);
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        if (!StringUtils.isEmpty(key)) {  // 如果key不为空
            wrapper.and(obj -> {
                obj.eq("attr_group_id", key).or().like("attr_group_name", key);
            });
        }
        if (catelogId == 0) {
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        } else {
            wrapper.eq("catelog_id", catelogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        }
    }

    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        // 1、查询分组信息
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        // 2、查询所有属性
        List<AttrGroupWithAttrsVo> collect = attrGroupEntities.stream().map(group -> {
            AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(group, attrGroupWithAttrsVo);

            List<AttrEntity> relationAttr = attrService.getRelationAttr(attrGroupWithAttrsVo.getAttrGroupId());
            attrGroupWithAttrsVo.setAttrs(relationAttr);
            return attrGroupWithAttrsVo;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        // 1、查出当前spu对应的所有属性的分组信息，以及当前分组下的所有属性对应的值
        //   SELECT pav.spu_id,ag.attr_group_name,ag.attr_group_id,aar.attr_id,a.attr_name,pav.attr_value FROM pms_attr_group ag LEFT JOIN pmg_attr_attrgroup_relation aar ON ag.attr_group_id=aar.attr_group_id LEFT JOIN pms_attr a ON aar.attr_id=a.attr_id LEFT JOIN pms_product_attr_value pav ON pav.attr_id=a.attr_id WHERE ag.catelog_id = #{catalogId} AND pav.spu_id=#{spuId};
        List<SpuItemAttrGroupVo> vos = this.getBaseMapper().getAttrGroupWithAttrsBySpuId(spuId, catalogId);
        return vos;
    }

}