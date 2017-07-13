package com.opc.freshness.domain.po;

import lombok.Data;

import java.util.Date;

@Data
public class BatchPo {
    private Integer id;

    private Integer kindsId;

    private Integer shopId;

    private String shopName;

    private Integer status;

    private Date expiredTime;

    private Date expiredRealTime;

    private Date createTime;

}