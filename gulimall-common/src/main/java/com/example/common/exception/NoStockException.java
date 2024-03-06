package com.example.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @author taoao
 */
public class NoStockException extends RuntimeException {

    @Getter
    @Setter
    private Long skuId;

    public NoStockException(Long skuId) {
        super("商品id" + skuId + "没有足够的库存了");
    }

    public NoStockException(String msg) {
        super("商品id" + msg + "没有足够的库存了");
    }
}
