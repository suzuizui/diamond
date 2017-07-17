package com.opc.freshness.domain.po;

import lombok.Data;

import java.util.Date;

/**
 * 设备和品类 关系
 */
@Data
public class DeviceKindPo {
    /**
     * id
     */
    private Integer id;
    /**
     * 设备编号
     */
    private String deviceId;
    /**
     * 种类Id
     */
    private Integer kindId;
    /**
     * 创建时间
     */
    private Date createTime;

}