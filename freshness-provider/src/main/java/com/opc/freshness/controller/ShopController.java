package com.opc.freshness.controller;

import com.opc.freshness.common.Error;
import com.opc.freshness.common.Result;
import com.opc.freshness.common.Success;
import com.opc.freshness.domain.dto.BatchDto;
import com.opc.freshness.domain.vo.ShopVo;
import com.opc.freshness.service.biz.BatchBiz;
import com.wormpex.biz.lang.Biz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by qishang on 2017/7/12.
 */
@RestController
public class ShopController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(ShopController.class);
    @Autowired
    private BatchBiz batchBiz;

    /**
     * 设备与门店定位
     *
     * @param deviceId 设备ID
     * @return
     */
    @RequestMapping(value = "/api/shop/position/v1", method = RequestMethod.GET)
    public Result postitionByDeviceId(@RequestParam String deviceId) {
        return new Success(new ShopVo());
    }

    /**
     * 门店待废弃列表
     *
     * @param shopId
     * @return
     */
    @RequestMapping(value = "/api/shop/expire/list", method = {RequestMethod.GET})
    public Result getAbortList(@RequestParam Integer shopId) {
        return new Success("成功");
    }

    /**
     * 根据条形码获取SKU
     *
     * @param barCode 条形码
     * @return
     */
    @RequestMapping(value = "/api/shop/sku/{barCode}/v1", method = RequestMethod.GET)
    public Result skuByBarCode(@PathVariable String barCode) {
        return new Success(new ShopVo());
    }

    /**
     * 批次制作
     *
     * @param batchDto
     * @return
     */
    @RequestMapping(value = "/api/batch/sku/option/v1", method = {RequestMethod.POST})
    public Result addSku(@RequestBody BatchDto batchDto) {
        switch (batchDto.getOption()) {
            case 1: //制作
                return new Success(batchBiz.addBatch(batchDto));
            case 2: //报损
                return new Success("报损");
            case 3: //废弃
                return new Success("废弃");
            default:
                return new Error("不支持的操作");
        }
    }
}
