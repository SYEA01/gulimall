package com.example.gulimall.seckill.controller;

import com.example.common.utils.R;
import com.example.gulimall.seckill.service.SeckillService;
import com.example.gulimall.seckill.to.SecKillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SeckillController {

    @Autowired
    SeckillService seckillService;

    /**
     * 返回当前时间可以参与的秒杀商品信息
     *
     * @return
     */
    @GetMapping("/currentSeckillSkus")
    public R getCurrentSeckillSkus() {
        List<SecKillSkuRedisTo> vos = seckillService.getCurrentSeckillSkus();
        return R.ok().setData(vos);
    }

    /**
     * 查询当前sku是否参与秒杀优惠
     *
     * @param skuId
     * @return
     */
    @GetMapping("/sku/seckill/{skuId}")
    public R getSkuSeckillInfo(@PathVariable Long skuId) {
        SecKillSkuRedisTo to = seckillService.getSkuSeckillInfo(skuId);
        return R.ok().setData(to);
    }

    /**
     * 秒杀请求
     *
     * @param killId
     * @return
     */
    @GetMapping("/kill")
    public R secKill(@RequestParam String killId,
                     @RequestParam String key,
                     @RequestParam Integer num) {
        // 1、也得判断是否登录  【 拦截器。。。 】

        // 2、
        String orderSn = seckillService.kill(killId, key, num);
        return R.ok().setData(orderSn);
    }
}
