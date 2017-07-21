package com.opc.freshness.api.model.dto;

import lombok.Data;

/**
 * Created by qishang on 2017/7/13.
 * sku 数据传输对象
 */
@Data
public class SkuDto {
    /**
     * 商品SkuId
     */
    private Integer skuId;
    /**
     * 数量
     */
    private Integer quantity;
}
