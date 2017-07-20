package com.opc.freshness.controller;

import com.opc.freshness.common.Result;
import com.opc.freshness.common.Success;
import com.opc.freshness.service.SkuService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/20
 */
@RestController
public class SkuController {
    @Resource
    private SkuService saleService;

    /**
     * 添加sku销量预测信息
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
    @RequestMapping(value = " /api/sales/predict/add", method = RequestMethod.POST)
    public Result<Boolean> salePredictAdd(
            @RequestParam Integer shopId,
            @RequestParam String shopName,
            @RequestParam Integer skuId,
            @RequestParam String skuName,
            @RequestParam Integer peakTime,
            @RequestParam Integer adviseCount,
            @RequestParam Date saleDay) {
        return new Success<Boolean>(saleService.salePredictAdd(shopId, shopName, skuId, skuName, peakTime, adviseCount, saleDay));
    }

    /**
     * 添加sku特殊规则
     * @param skuId
     * @param kindId
     * @param delay
     * @param expired
     * @return
     */
    @RequestMapping(value = " /api/sku/rules/add", method = RequestMethod.POST)
    public Result<Boolean> addSkuTime(
            @RequestParam Integer skuId,
            @RequestParam Integer kindId,
            @RequestParam Integer delay,
            @RequestParam Integer expired) {
        return new Success<Boolean>(saleService.addSkuTime(skuId,kindId,delay,expired));
    }
}
