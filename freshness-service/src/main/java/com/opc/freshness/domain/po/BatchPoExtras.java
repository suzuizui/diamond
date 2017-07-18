package com.opc.freshness.domain.po;


import lombok.Builder;
import lombok.Data;
/**
 * AUTHOR: qishang
 * DATE: 2017/7/17
 */

/**
 * 拓展字段结构
 */
@Data
@Builder
public class BatchPoExtras {
    /**
     * 温度
     */
    private Integer degree;
    /**
     * 颜色
     */
    private String tag;
    /**
     * 颜色
     */
    private String unit;
}
