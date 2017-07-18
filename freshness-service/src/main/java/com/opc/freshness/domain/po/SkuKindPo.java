package com.opc.freshness.domain.po;

import lombok.Data;

import java.util.Date;

/**
 * sku和品类 关联
 */
@Data
public class SkuKindPo {
    /**
     * id
     */
    private Integer id;
    /**
     * skuId
     */
    private Integer skuId;
    /**
     * sku 名称
     */
    private String skuName;
    /**
     * 图片地址
     */
    private String imgUrl;
    /**
     * 门店Id
     */
    private Integer shopId;
    /**
     * 种类Id
     */
    private Integer kindId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 最后修改时间
     */
    private Date lastMofifyTime;

}