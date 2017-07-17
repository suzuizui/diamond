package com.opc.freshness.controller;

import com.opc.freshness.common.Error;
import com.opc.freshness.common.Result;
import com.opc.freshness.common.Success;
import com.opc.freshness.common.util.CollectionUtils;
import com.opc.freshness.domain.dto.BatchDto;
import com.opc.freshness.domain.vo.ShopVo;
import com.opc.freshness.domain.vo.SkuVo;
import com.opc.freshness.service.biz.BatchBiz;
import com.opc.freshness.service.integration.ShopService;
import com.wormpex.cvs.product.api.bean.BeeShop;
import io.swagger.annotations.Api;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by qishang on 2017/7/12.
 */
@Api
@RestController
public class ShopController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(ShopController.class);
    @Autowired
    private BatchBiz batchBiz;
    @Autowired
    private ShopService shopService;

    /**
     * 设备与门店定位
     *
     * @param deviceId 设备ID
     * @return
     */
    @RequestMapping(value = "/api/shop/position/v1", method = RequestMethod.GET)
    public Result postitionByDeviceId(@RequestParam String deviceId) {
        BeeShop shop = shopService.queryByDevice(deviceId);
        return new Success(
                ShopVo.
                        builder().
                        shopId(shop.getShopId()).
                        shopName(shop.getPropInfo().getDisplayName())
                        .build());
    }

    /**
     * 门店待废弃列表
     *
     * @param shopId
     * @return
     */
    @RequestMapping(value = "/api/shop/expire/list/v1", method = {RequestMethod.GET})
    public Result getAbortList(@RequestParam Integer shopId) {
        // TODO: 2017/7/13
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
        // TODO: 2017/7/13

        return new Success(new SkuVo());
    }

    /**
     * 操作批次
     *
     * @param batchDto
     * @return
     */
    @RequestMapping(value = "/api/batch/sku/option/v1", method = {RequestMethod.POST})
    public Result addSku(@RequestBody BatchDto batchDto) {
        Asserts.notNull(batchDto.getShopId(), "店铺Id");
        Asserts.notNull(batchDto.getOption(), "操作类型");
        Asserts.notNull(batchDto.getCreateTime(), "操作时间");

        Asserts.notEmpty(batchDto.getOperator(), "操作员");
        CollectionUtils.notEmpty(batchDto.getSkuList(), "sku列表");
        batchDto.getSkuList().forEach(skuDto -> {
            Asserts.notNull(skuDto.getSkuId(), "skuId");
            Asserts.notNull(skuDto.getQuantity(), "sku数量");
        });
        // TODO: 2017/7/13
        switch (batchDto.getOption()) {
            case 1: //制作
                Asserts.notEmpty(batchDto.getKindCode(), "分类Id");
                return new Success(batchBiz.addBatch(batchDto));
            case 2: //报损
                Asserts.notNull(batchDto.getBatchId(), "批次号");
                return new Success(batchBiz.batchLoss(batchDto));
            case 3: //废弃
                Asserts.notNull(batchDto.getBatchId(), "批次号");
                return new Success(batchBiz.batchAbort(batchDto));
            default:
                return new Error("不支持的操作");
        }
    }
}
