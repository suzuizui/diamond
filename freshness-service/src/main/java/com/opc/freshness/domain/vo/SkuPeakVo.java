package com.opc.freshness.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/20
 * 高峰信息
 */
@Data
@Builder
public class SkuPeakVo {
    /**
     * 高峰Id
     */
    private Integer id;
    /**
     * 高峰名称
     */
    private String name;
    /**
     * 预测销量
     */
    private Integer adviseCount;
    /**
     * 已制作数量
     */
    private Integer makeCount;
}
