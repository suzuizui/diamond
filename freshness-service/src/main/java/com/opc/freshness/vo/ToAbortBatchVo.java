package com.opc.freshness.vo;

import lombok.Data;

/**
 * Created by qishang on 2017/7/13.
 * 待废弃批次Vo
 */
@Data
public class ToAbortBatchVo {
    /**
     * 批次Id
     */
    private String batchId;
    /**
     * skuName
     */
    private String skuName;
    /**
     * 数量
     */
    private int quanity;
    /**
     * 颜色
     */
    private String tag;

    /**
     * 过期时间
     */
    private int delayed;
}
