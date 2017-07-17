package com.opc.freshness.api.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by qishang on 2017/7/13.
 * sku 数据传输对象
 */
@Data
public class SkuDto {
    /**
     * 商品SkuId
     */
    @NotNull(message = "skuId不能为空")
    private Integer skuId;
    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    private Integer quantity;
}
