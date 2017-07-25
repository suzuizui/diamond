package com.opc.freshness.domain.po;

import lombok.Data;

import java.util.Date;

@Data
public class BatchStatePo {
    private Integer id;
    /**
     * 批次Id
     */
    private Integer batchId;
    /**
     * 门店Id
     */
    private Integer shopId;
    /**
     * 状态
     *
     * @see BatchPo.status
     */
    private Integer status;
    /**
     * 商品Id
     */
    private Integer skuId;
    /**
     * sku名称
     */
    private String skuName;
    /**
     * 销售量
     */
    private Integer skuStock;
    /**
     * 图片地址
     */
    private String imgUrl;
    /**
     * 数量
     */
    private Integer quantity;
    /**
     * 操作人
     */
    private String operator;
    /**
     * 操作时间
     */
    private Date createTime;

}