package com.opc.freshness.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * Created by qishang on 2017/7/13.
 * 待废弃批次Vo
 */
@Data
@Builder
public class ToAbortBatchVo {
    /**
     * 批次Id
     */
    private Integer batchId;
    /**
     * 品类Id
     */
    private Integer categoryId;
    /**
     * 批次名称
     */
    private String batchName;
    /**
     * 数量
     */
    private int quanity;
    /**
     * 颜色
     */
    private String tag;

    /**
     * 预计废弃时间
     */
    private Date expiredTime;
}
