package com.opc.freshness.controller;

import com.opc.freshness.common.Result;
import com.opc.freshness.common.Success;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/20
 */
@RestController
public class SaleController {
    @RequestMapping(value = " /api/sales/predict/add", method = RequestMethod.POST)
    public Result<Boolean> salePredictAdd(Integer shopId, Integer shopName, Integer skuId, String skuName, Integer peakTime, Integer adviseCount, Date saleDay) {

        return new Success<Boolean>(Boolean.TRUE);
    }

}
