package com.opc.freshness.domain.po;

import lombok.Data;

import java.util.Date;

@Data
public class SalePredictPo {
    private Integer id;

    private Integer shopId;

    private String shopName;

    private Integer skuId;
    private String skuCode;
    private String skuName;

    private Integer peak;

    private Integer adviseCount;

    private Date salesDay;

    private Date createTime;

    private Date lastModifyTime;
}