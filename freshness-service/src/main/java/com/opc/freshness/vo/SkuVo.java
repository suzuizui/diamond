package com.opc.freshness.vo;

import lombok.Data;

/**
 * Created by qishang on 2017/7/13.
 * 商品sku
 */
@Data
public class SkuVo {
    /**
     * 商品SkuId
     */
    private Integer skuId;
    /**
     * 商品Sku名称
     */
    private String skuName;
    /**
     * 图片地址
     */
    private String imgUrl;
}
