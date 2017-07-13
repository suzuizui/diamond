package com.opc.freshness.domain.dto;

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
    private int skuId;
    /**
     * 商品Sku名称
     */
    private String skuName;
    /**
     * 图片地址
     */
    private String imgUrl;
    /**
     * 数量
     */
    private int quantity;
}
