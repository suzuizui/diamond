package com.opc.freshness.domain.po;

import lombok.Data;

/**
 * 种类
 */
@Data
public class KindPo {

    private Integer id;
    /**
     * 种类名称
     */
    private String name;
    /**
     * 过期时间
     */
    private Float expired;
    /**
     * 延期时间
     */
    private Integer delay;
    /**
     * 编码
     */
    private String code;
    /**
     * 排序字段
     */
    private Integer sort;
    /**
     * 配置字段
     */
    private String config;
}