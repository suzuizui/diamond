package com.opc.freshness.service.integration;

import com.wormpex.cvs.product.api.bean.BeeShop;

/**
 * Created by qishang on 2017/7/12.
 * 门店第三方服务
 */
public interface ShopService {
    /**
     * 通过Id查找门店
     *
     * @param shopId
     * @return
     */
    BeeShop queryById(int shopId);

    /**
     * 根据设备id查询门店信息
     *
     * @param deviceId
     * @return
     */
    BeeShop queryByDevice(String deviceId);
}
