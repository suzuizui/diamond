package com.opc.freshness.domain.bo;

import lombok.Data;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/24
 */
@Data
public class OrderMachineBo {
    private ShopBo shopBo;
    private DeviceBo deviceInfo;
    private DeviceBo relevantDeviceInfo;
}
