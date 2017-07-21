package com.opc.freshness.domain.po;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * AUTHOR: qishang
 * DATE: 2017/7/17
 */

/**
 * 拓展字段结构
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
