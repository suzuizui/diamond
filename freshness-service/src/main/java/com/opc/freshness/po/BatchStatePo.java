package com.opc.freshness.po;

import lombok.Data;

import java.util.Date;
@Data
public class BatchStatePo {
    private Integer id;

    private Integer batchId;

    private Integer status;

    private Integer skuId;

    private String skuName;

    private Integer skuStock;

    private String imgUrl;

    private Integer quantity;

    private String operator;

    private Date createTime;

    private String tag;
    /**
     * 单位
     */
    private String unit;
    /**
     * 温度
     */
    private Integer degree;

}