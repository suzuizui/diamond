package com.opc.freshness.domain.bo;

import lombok.Data;

import java.util.List;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/24
 */
@Data
public class OrderMachineBo {
    private ShopBo shopInfo;
    private DeviceBo deviceInfo;
    private List<DeviceBo> relevantDeviceInfo;
}
