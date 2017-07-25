package com.opc.freshness.service;

import com.opc.freshness.domain.po.SalePredictPo;
import com.opc.freshness.domain.vo.SkuVo;

import java.util.Date;
import java.util.List;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/20
 * 销量服务
 */
public interface SkuService {
    /**
     * 添加销量预测信息
     *
     * @param shopCode
     * @param shopName
     * @param productCode
     * @param skuName
     * @param peakTime
     * @param adviseCount
     * @param saleDay
     * @return
     */
    Boolean salePredictAdd(String shopCode, String shopName, String productCode, String skuName, Integer peakTime, Integer adviseCount, Date saleDay);

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

    /**
     * 查询
     * @param shopId
     * @param date
     * @return
     */
    List<SalePredictPo> selectPredic(Integer shopId, Date date);
}
