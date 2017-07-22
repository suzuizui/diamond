package com.opc.freshness.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Created by qishang on 2017/7/17.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    /**
     * 配置信息Map
     */
    private Map<String,Object> configObj;
}
