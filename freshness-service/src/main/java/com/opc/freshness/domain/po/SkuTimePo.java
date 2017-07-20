package com.opc.freshness.domain.po;

import lombok.Data;

import java.util.Date;

@Data
public class SkuTimePo {
    private Integer id;

    private Integer skuId;

    private Integer kindId;

    private Integer delay;

    private Integer expired;

    private Date createTime;

    private Date lastModifyTime;
}