package com.opc.freshness.service.impl;

import com.opc.freshness.service.SkuService;
import com.opc.freshness.service.biz.SkuBiz;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/20
 */
@Service
public class SkuServiceImpl implements SkuService {
    @Resource
    private SkuBiz SkuBiz;

    @Override
    public Boolean salePredictAdd(Integer shopId, String shopName, Integer skuId, String skuName, Integer peakTime, Integer adviseCount, Date saleDay) {
        return SkuBiz.addSalePredict(shopId, shopName,skuId, skuName, peakTime, adviseCount, saleDay);
    }

    @Override
    public Boolean addSkuTime(Integer skuId, Integer kindId, Integer delay, Integer expired) {
        return SkuBiz.addSkuTime(skuId,kindId,delay,expired);
    }
}
