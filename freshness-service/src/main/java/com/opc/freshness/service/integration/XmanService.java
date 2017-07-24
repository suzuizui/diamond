package com.opc.freshness.service.integration;

import com.opc.freshness.domain.bo.OrderMachineBo;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/24
 * xman服务
 */
public interface XmanService {
    /**
     * 查询设备相关信息
     *
     * @param deviceId
     * @return
     */
    OrderMachineBo relevantInfo(String deviceId);
}
