package com.opc.freshness.domain.bo;

import lombok.Data;

import java.util.Date;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/19
 */
@Data
public class SkuDetailBo {
    private Integer id;
    /**
     * sku编号
     */
    private Integer skuId;
    /**
     * sku名称
     */
    private String skuName;
    /**
     * 总个数
     */
    private Integer totalCount;
    /**
     * 制作人
     */
    private String maker;
    /**
     * 制作时间
     */
    private Date createTime;
    /**
     * 拓展字段
     */
    private String extras;
    /**
     * 预计废弃时间
     */
    private Date expiredTime;
    /**
     * 实际废弃时间
     */
    private Date expiredRealTime;
    /**
     * 废弃个数
     */
    private Integer expiredCount;
    /**
     * 废弃人
     */
    private String aborter;
    /**
     * 图片地址
     */
    private String imgUrl;
}
