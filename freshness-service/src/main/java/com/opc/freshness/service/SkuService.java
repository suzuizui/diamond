package com.opc.freshness.service;

import com.opc.freshness.domain.vo.SkuVo;

import java.util.Date;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/20
 * 销量服务
 */
public interface SkuService {
    /**
     * 添加销量预测信息
     *
     * @param shopId
     * @param shopName
     * @param skuId
     * @param skuName
     * @param peakTime
     * @param adviseCount
     * @param saleDay
     * @return
     */
    Boolean salePredictAdd(Integer shopId, String shopName, Integer skuId, String skuName, Integer peakTime, Integer adviseCount, Date saleDay);

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

    /**
     * 通过条形码查询Sku信息
     *
     * @param barCode
     * @return
     */
    SkuVo selectSkuByBarCode(String barCode, Integer shopId);
}
