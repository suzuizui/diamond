package com.opc.freshness.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * Created by qishang on 2017/7/17.
 */
@Data
public class KindVo {
    /**
     * 品类id
     */
    private Integer id;
    /**
     * 品类名称
     */
    private String name;
    /**
     * 配置信息
     */
    private String config;
}
