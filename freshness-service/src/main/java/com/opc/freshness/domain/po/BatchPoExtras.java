package com.opc.freshness.domain.po;

/**
 * Created by qishang on 2017/7/17.
 */

import lombok.Builder;
import lombok.Data;

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
