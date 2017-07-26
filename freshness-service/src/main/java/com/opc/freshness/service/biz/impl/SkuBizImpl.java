package com.opc.freshness.service.biz.impl;

import com.opc.freshness.domain.po.SalePredictPo;
import com.opc.freshness.domain.po.SkuTimePo;
import com.opc.freshness.service.biz.SkuBiz;
import com.opc.freshness.service.dao.SalePredictMapper;
import com.opc.freshness.service.dao.SkuTimeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/20
 */
@Service
public class SkuBizImpl implements SkuBiz {
    @Resource
    private SalePredictMapper salePredictMapper;
    @Resource
    private SkuTimeMapper skuTimeMapper;

    @Override
    public Boolean addSalePredict(Integer shopId, String shopName, Integer skuId, String skuCode,String skuName, Integer peakTime, Integer adviseCount, Date saleDay) {
        SalePredictPo po = new SalePredictPo();
        po.setShopId(shopId);
        po.setShopName(shopName);
        po.setSkuId(skuId);
        po.setSkuName(skuName);
        po.setAdviseCount(adviseCount);
        po.setSalesDay(saleDay);
        po.setPeak(peakTime);
        salePredictMapper.save(po);
        return true;
    }

    @Override
    public Boolean addSkuTime(Integer skuId, Integer kindId, Integer delay, Integer expired) {
        SkuTimePo po = new SkuTimePo();
        po.setSkuId(skuId);
        po.setKindId(kindId);
        po.setDelay(delay);
        po.setExpired(expired);
        skuTimeMapper.insert(po);
        return true;
    }

    @Override
    public SkuTimePo selectRuleBySkuIdAndKindId(Integer SkuId, Integer kindId) {
        return skuTimeMapper.selectBySkuIdAndKindId(SkuId,kindId);
    }
    @Override
    public List<SalePredictPo> selectByRecord(SalePredictPo po){
        return salePredictMapper.selectByRecord(po);
    }
}
