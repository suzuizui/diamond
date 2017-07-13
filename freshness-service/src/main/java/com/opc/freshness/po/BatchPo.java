package com.opc.freshness.po;

import lombok.Data;

import java.util.Date;

@Data
public class BatchPo {
    /**
     * id
     */
    private Integer id;
    /**
     * 种类Id
     */
    private Integer kindsId;
    /**
     * 门店Id
     */
    private Integer shopId;
    /**
     * 门店名称
     */
    private String shopName;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 预计废弃时间
     */
    private Date expiredTime;
    /**
     * 实际废弃时间
     */
    private Date expiredRealTime;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 批次总数量
     */
    private Integer totalCount;
    /**
     * 废弃数量
     */
    private Integer expiredCount;
    /**
     * 报损数量
     */
    private Integer breakCount;

}