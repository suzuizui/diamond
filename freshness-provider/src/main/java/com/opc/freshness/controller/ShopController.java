package com.opc.freshness.controller;

import com.opc.freshness.common.Result;
import com.opc.freshness.common.Success;
import com.opc.freshness.domain.dto.AddSkuDto;
import com.opc.freshness.domain.vo.ShopVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * Created by qishang on 2017/7/12.
 */
@RestController
public class ShopController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(ShopController.class);

    /**
     * 设备与门店定位
     *
     * @param deviceId 设备ID
     * @return
     */
    @RequestMapping(value = "/api/shop/position", method = RequestMethod.GET)
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
    @RequestMapping(value = "/api/shop/sku/{barCode}", method = RequestMethod.GET)
    public Result skuByBarCode(@PathVariable String barCode) {
        return new Success(new ShopVo());
    }

    /**
     * 批次制作
     *
     * @param skuDto
     * @return
     */
    @RequestMapping(value = "/api/batch/addSku", method = {RequestMethod.POST})
    public Result addSku(@RequestBody AddSkuDto skuDto) {

        return new Success("成功");
    }

    /**
     * 批次报损
     *
     * @param shopId   店铺Id
     * @param skuId    SkuId
     * @param quantity 数量
     * @param operator 操作人
     * @param batch    批次
     * @return
     */
    @RequestMapping(value = "/api/batch/addSku", method = {RequestMethod.POST})
    public Result breakSku(@RequestParam String shopId,
                           @RequestParam String skuId,
                           @RequestParam String quantity,
                           @RequestParam String operator,
                           @RequestParam String batch) {
        return new Success("成功");
    }

}
