package com.opc.freshness.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by qishang on 2017/7/13.
 * sku 数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
