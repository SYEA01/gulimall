package com.example.common.to.mq;

import lombok.Data;

/**
 * @author taoao
 */
@Data
public class StockLockedTo {
    /**
     * 库存工作单id
     */
    private Long id;

    /**
     * 工作单详情
     */
    private StockDetailTo detail;

}
