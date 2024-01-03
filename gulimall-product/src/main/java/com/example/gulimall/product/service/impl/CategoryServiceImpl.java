package com.example.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;
import com.example.gulimall.product.dao.CategoryDao;
import com.example.gulimall.product.entity.CategoryEntity;
import com.example.gulimall.product.service.CategoryBrandRelationService;
import com.example.gulimall.product.service.CategoryService;
import com.example.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

//    private Map<String, Object> cache = new HashMap<>();

//    @Autowired
//    CategoryDao categoryDao;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redisson;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 1、查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        // 2、组装成父子的树形结构
        // 2.1）、找到所有的1级分类
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity -> categoryEntity.getParentCid() == 0).map(menu -> {
            // 2.2）、 递归设置子菜单
            menu.setChildren(getChildrens(menu, entities));
            return menu;
            // 2.3）、给所有的一级菜单排序
        }).sorted((preMenu, nextMenu) -> {  // (前一个菜单,后一个菜单)
            return (preMenu.getSort() == null ? 0 : preMenu.getSort()) - (nextMenu.getSort() == null ? 0 : nextMenu.getSort());
        }).collect(Collectors.toList());


        return level1Menus;
    }


    /**
     * 递归查询所有菜单的子菜单
     *
     * @param root 当前菜单
     * @param all  所有菜单
     * @return
     */
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid().equals(root.getCatId()))
                .map(categoryEntity -> {
                    // 1、找到子菜单
                    categoryEntity.setChildren(getChildrens(categoryEntity, all));  // 为当前菜单的子菜单设置子菜单
                    return categoryEntity;
                }).sorted((preMenu, nextMenu) -> {
                    // 2、菜单的排序
                    return (preMenu.getSort() == null ? 0 : preMenu.getSort()) - (nextMenu.getSort() == null ? 0 : nextMenu.getSort());
                }).collect(Collectors.toList());
        return children;
    }

    @Override
    public void removeMenuByIds(List<Long> list) {
        //TODO   1、检查当前删除的菜单，是否被别的地方引用

        // 2、逻辑删除
        baseMapper.deleteBatchIds(list);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);
        return (Long[]) parentPath.toArray(new Long[parentPath.size()]);
    }

    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());

        // 双写模式：把数据库改完，同时修改缓存中的数据
        // 失效模式：把数据库改完，同时删除缓存中的数据  redis.del("catalogJSON");  然后等待下一次查询的时候，主动更新
    }


    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        // 1、收集当前节点id
        paths.add(catelogId);
        CategoryEntity categoryEntity = this.getById(catelogId);

        if (categoryEntity.getParentCid() != 0) {
            findParentPath(categoryEntity.getParentCid(), paths);
        }
        return paths;
    }

    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        long start = System.currentTimeMillis();
        List<CategoryEntity> entities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        long end = System.currentTimeMillis();
        System.out.println("消耗时间： " + (end - start));
        return entities;
    }

    // TODO 产生堆外内存溢出：OutOfDirectMemoryError
    // 1、SpringBoot2.0 默认使用lettuce作为操作Redis的客户端。lettuce使用netty进行网络通信。
    // 2、主要原因是lettuce的bug 导致堆外内存溢出   -Xmx300m   netty如果没有指定堆外内存，默认使用 -Xmx300m 作为堆外内存
    //  可以通过 -Dio.netty.maxDirectMemory 进行设置堆外内存
    // 解决方案： 不能使用-Dio.netty.maxDirectMemory 只去调大堆外内存。
    //  方案1：升级lettuce 客户端。  方案2：切换使用jedis作为操作Redis的客户端
    //
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        // 给缓存中放JSON字符串，拿出的JSON字符串还要逆转为能用的对象类型； 【 序列化与反序列化 】

        /**
         * 1、加上空结果缓存：解决缓存穿透问题
         * 2、设置过期时间（加随机值）：解决缓存雪崩问题
         * 3、加锁：解决缓存击穿问题
         */


        // 1、加入缓存逻辑，缓存中存的数据是JSON字符串
        // JSON的好处：跨语言跨平台兼容
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catalogJSON)) {
            // 2、缓存中没有，查询数据库
            System.out.println("缓存不命中。。。。。。将要查询数据库。。。。。。");
            Map<String, List<Catelog2Vo>> catalogJsonFromDb = getCatalogJsonFromDbWithRedisLock();

            return catalogJsonFromDb;
        }

        System.out.println("缓存命中。。。。。。直接返回。。。。。。");
        // 把从缓存中查到的字符串转为指定的对象
        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });

        return result;
    }

    /**
     * 缓存中的数据如何和数据库保持一致 【 缓存数据一致性问题 】
     * 场景：
     *      1）、双写模式
     *      2）、失效模式
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedissonLock() {

        // 1、锁的名字   锁的粒度越细越快；
        // 约定：锁的粒度：具体缓存的是某个数据，  【 比如：11号商品： product-11-lock 】
        RLock lock = redisson.getLock("catalogJson-lock");
        lock.lock();

        Map<String, List<Catelog2Vo>> dataFromDb;
        try {
            dataFromDb = getDataFromDb();
        } finally {
            lock.unlock();
        }
        return dataFromDb;


    }

    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedisLock() {

        // 1、占分布式锁。去Redis占坑  ， 可以边加锁 边设置过期时间 30秒
        // 3、value设置成唯一标识，可以在删除锁的时候指定，可以避免被其他线程删除掉
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("获取分布式锁成功,,,");
            // 加锁成功...  执行业务
            // 2、设置过期时间   30秒自动过期  【 可以避免死锁 】
//            redisTemplate.expire("lock", 30, TimeUnit.SECONDS);
            Map<String, List<Catelog2Vo>> dataFromDb = null;
            try {
                dataFromDb = getDataFromDb();
            } finally {
                // 4、获取值对比 + 对比成功删除  ===== 也得是个原子操作   【 Lua脚本解锁 】
//            String lockValue = redisTemplate.opsForValue().get("lock");
//            if (uuid.equals(lockValue)) {
//                redisTemplate.delete("lock");  // 执行完业务之后删除锁
//            }
                // Lua脚本 删除锁
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                Long lock1 = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class)
                        , Arrays.asList("lock"), uuid);
            }


            return dataFromDb;
        } else {
            // 加锁失败   重试。
            // 休眠100毫秒 重试
            System.out.println("获取分布式锁失败,,, 等待重试");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
            return getCatalogJsonFromDbWithRedisLock();  // 自旋的方式
        }


    }

    private Map<String, List<Catelog2Vo>> getDataFromDb() {
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.isEmpty(catalogJSON)) {
            // 如果缓存不为空，直接返回
            Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            return result;
        }
        System.out.println("查询了数据库。。。。。。");

        List<CategoryEntity> allList = baseMapper.selectList(null);

        List<CategoryEntity> level1Categorys = getParentCid(allList, 0L);

        // 2、封装数据
        Map<String, List<Catelog2Vo>> map = level1Categorys.stream().collect(Collectors.toMap(key -> key.getCatId().toString(), value -> {
            // 1、每一个的一级分类，查到这个一级分类的所有二级分类
            List<CategoryEntity> category2List = getParentCid(allList, value.getParentCid());
            // 2、封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (category2List != null) {
                catelog2Vos = category2List.stream().map(c2 -> {
                    // 1、找当前分类的三级分类封装成Vo
                    List<CategoryEntity> category3List = getParentCid(allList, c2.getCatId());
                    List<Catelog2Vo.Catelog3Vo> catelog3Vos = null;
                    if (category3List != null) {
                        catelog3Vos = category3List.stream().map(c3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(c2.getCatId().toString(), c3.getCatId().toString(), c3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                    }
                    Catelog2Vo catelog2Vo = new Catelog2Vo(value.getCatId().toString(), catelog3Vos, c2.getCatId().toString(), c2.getName());
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        // 3、将查到的数据再放入缓存,将对象转为JSON放到缓存中
        String jsonString = JSON.toJSONString(catalogJSON);
//            redisTemplate.opsForValue().set("catalogJSON", jsonString);
        redisTemplate.opsForValue().set("catalogJSON", jsonString, 1, TimeUnit.DAYS);  // 缓存的过期时间 1天
        return map;
    }


    // 从数据库查询并封装整个分类数据
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithLocalLock() {

//        // 1、如果缓存中有，就用缓存的
//        Map<String, List<Catelog2Vo>> catalogJson = (Map<String, List<Catelog2Vo>>) cache.get("catalogJson");
//        if (catalogJson == null) {
//            // 调用业务，返回数据又放入缓存
//            cache.put("catalogJson", map);
//        }
//        return catalogJson;

        // 只要是同一把锁，就能锁住需要这个锁的所有线程
        // 1、synchronized (this) ：SpringBoot 所有的组件在容器中都是单例的，
        // TODO 本地锁： synchronized、JUC（Lock） ； 在分布式情况下，想要锁住所有，就必须使用分布式锁

        synchronized (this) {
            return getDataFromDb();
        }
    }

    private List<CategoryEntity> getParentCid(List<CategoryEntity> allList, Long parentCid) {
//        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", value.getCatId()));
        List<CategoryEntity> collect = allList.stream().filter(item -> item.getParentCid().equals(parentCid)).collect(Collectors.toList());
        return collect;
    }

}