package com.opc.freshness.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by qishang on 2017/7/13.
 * 商品sku
 */
@Data
@Builder
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
    /**
     * 个数
     */
    private Integer count;
    /**
     * 废弃数量
     */
    private Integer abortCount;
    /**
     * 品类信息
     */
    private List<KindVo> categories;
    /**
     * 是否设置过品类
     */
    private Boolean hasCategory;
    /**
     * 高峰信息
     */
    private SkuPeakVo peakInfo;
}
