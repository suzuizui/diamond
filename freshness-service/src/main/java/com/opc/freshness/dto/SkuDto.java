package com.opc.freshness.dto;

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
     * 商品Sku名称
     */
    @NotNull(message = "sku名称不能为空")
    private String skuName;
    /**
     * 图片地址
     */
    private String imgUrl;
    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    private Integer quantity;
}
