package com.opc.freshness.service.integration.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.opc.freshness.service.integration.ShopService;
import com.wormpex.cvs.product.api.bean.BeeShop;
import com.wormpex.cvs.product.api.remote.ShopRemote;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/12.
 */
@Service
public class ShopServiceImpl implements ShopService {
    @Resource
    private ShopRemote shopRemote;

    /**
     * 通过Id查询门店
     * @param id
     * @return
     */
    public BeeShop queryById(int id) {
        return shopRemote.queryById(id);
    }

    /**
     * 根据设备id查询门店信息
     * @param deviceId
     * @return
     */
    @Override
    public BeeShop queryByDevice(String deviceId){
        return shopRemote.queryByDevice(deviceId);
    }

    /**
     * 通过code查询
     * @param shopCode
     * @return
     */
    @Override
    public BeeShop queryByCode(String shopCode) {
        return shopRemote.queryByCode(shopCode);
    }
}
