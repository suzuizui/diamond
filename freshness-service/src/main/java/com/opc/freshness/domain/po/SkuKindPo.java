package com.opc.freshness.domain.po;

import lombok.Data;

import java.util.Date;

@Data
/**
 * sku和品类 关联
 */
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