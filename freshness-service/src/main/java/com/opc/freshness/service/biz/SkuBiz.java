package com.opc.freshness.service.biz;

import com.opc.freshness.domain.po.SkuTimePo;

import java.util.Date;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/20
 */
public interface SkuBiz {
    /**
     * 添加销量预测记录
     *
     * @param shopId
     * @param shopName
     * @param skuName
     * @param peakTime
     * @param adviseCount
     * @param saleDay
     * @return
     */
    Boolean addSalePredict(Integer shopId, String shopName, Integer skuId, String skuName, Integer peakTime, Integer adviseCount, Date saleDay);

    /**
     * 添加sku特殊规则
     *
     * @param skuId
     * @param kindId
     * @param delay
     * @param expired
     * @return
     */
    Boolean addSkuTime(Integer skuId, Integer kindId, Integer delay, Integer expired);

    SkuTimePo selectRuleBySkuIdAndKindId(Integer SkuId,Integer kindId);
}
