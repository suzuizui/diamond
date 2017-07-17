package com.opc.freshness.service.integration.impl;

import com.opc.freshness.service.integration.ShopService;
import com.wormpex.cvs.product.api.bean.BeeShop;
import com.wormpex.cvs.product.api.remote.ShopRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by qishang on 2017/7/12.
 */
@Service
public class ShopServiceImpl implements ShopService {
    private final static Logger logger = LoggerFactory.getLogger(ShopServiceImpl.class);
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
}
