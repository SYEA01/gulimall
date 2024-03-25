package com.example.gulimall.product.feign.fallback;


import com.example.common.exception.BizCodeEnume;
import com.example.common.utils.R;
import com.example.gulimall.product.feign.SecKillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 添加一个SecKillFeignService 接口的实现
 */
@Slf4j
@Component
public class SecKillFeignServiceFallBack implements SecKillFeignService {
    @Override
    public R getSkuSeckillInfo(Long skuId) {
        log.info("getSkuSeckillInfo熔断方法调用：秒杀服务的远程调用失败了，调用了此方法。。。");
        return R.error(BizCodeEnume.TO_MANY_REQUEST.getCode(), BizCodeEnume.TO_MANY_REQUEST.getMessage());
    }
}
