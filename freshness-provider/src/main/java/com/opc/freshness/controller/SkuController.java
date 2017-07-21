package com.opc.freshness.controller;

import com.opc.freshness.common.Result;
import com.opc.freshness.common.Success;
import com.opc.freshness.domain.vo.SkuVo;
import com.opc.freshness.service.SkuService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/20
 */
@RestController
public class SkuController {
    @Resource
    private SkuService skuService;


    /**
     * 根据条形码获取SKU
     *
     * @param barCode 条形码
     * @param shopId  门店Id
     * @return
     */
    @RequestMapping(value = "/api/shop/sku/{barCode}/v1", method = RequestMethod.GET)
    public Result<SkuVo> skuByBarCode(@PathVariable String barCode,
                                      @RequestParam Integer shopId) {
        return new Success<SkuVo>(skuService.selectSkuByBarCode(barCode, shopId));
    }

    /**
     * 添加sku销量预测信息
     *
     * @param shopId
     * @param shopName
     * @param skuId
     * @param skuName
     * @param peakTime    1.早高峰 2.午高峰 3.晚高峰
     * @param adviseCount 预测个数
     * @param saleDay
     * @return
     */
    @RequestMapping(value = "/api/sku/predict/add/v1", method = RequestMethod.POST)
    public Result<Boolean> salePredictAdd(
            @RequestParam Integer shopId,
            @RequestParam String shopName,
            @RequestParam Integer skuId,
            @RequestParam String skuName,
            @RequestParam Integer peakTime,
            @RequestParam Integer adviseCount,
            @RequestParam Date saleDay) {
        return new Success<Boolean>(skuService.salePredictAdd(shopId, shopName, skuId, skuName, peakTime, adviseCount, saleDay));
    }

    /**
     * 添加sku特殊规则
     *
     * @param skuId
     * @param kindId
     * @param delay   开始制作后延迟时间 分钟
     * @param expired 过期时间 分钟
     * @return
     */
    @RequestMapping(value = "/api/sku/rules/add", method = RequestMethod.POST)
    public Result<Boolean> addSkuTime(
            @RequestParam Integer skuId,
            @RequestParam Integer kindId,
            @RequestParam Integer delay,
            @RequestParam Integer expired) {
        return new Success<Boolean>(skuService.addSkuTime(skuId, kindId, delay, expired));
    }
}
