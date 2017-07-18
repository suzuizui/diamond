package com.opc.freshness.domain.vo;

import java.util.Date;

import lombok.Data;

/**
 * Created by qishang on 2017/7/17.
 */
@Data
public class BatchLogVo {
    private Integer Id;
    /**
     * 批次Id
     */
    private Integer batchId;
    /**
     * skuId
     */
    private Integer skuId;
    /**
     * skuName
     */
    private String skuName;
    /**
     * 操作类型 1.制作 2.报损 3.废弃
     */
    private Integer operateType;
    /**
     * 数量
     */
    private Integer count;
    /**
     * 操作人
     */
    private String operatorName;
    /**
     * 操作时间
     */
    private Date operateTime;
}
