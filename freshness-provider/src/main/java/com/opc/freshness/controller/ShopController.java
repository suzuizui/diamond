package com.opc.freshness.controller;

import com.google.common.collect.Lists;
import com.opc.freshness.common.Error;
import com.opc.freshness.common.Result;
import com.opc.freshness.common.Success;
import com.opc.freshness.common.util.BeanCopyUtils;
import com.opc.freshness.common.util.CollectionUtils;
import com.opc.freshness.common.util.Pager;
import com.opc.freshness.domain.dto.BatchDto;
import com.opc.freshness.domain.dto.SkuKindDto;
import com.opc.freshness.domain.po.BatchPo;
import com.opc.freshness.domain.po.KindPo;
import com.opc.freshness.domain.vo.*;
import com.opc.freshness.service.biz.BatchBiz;
import com.opc.freshness.service.biz.KindBiz;
import com.opc.freshness.service.biz.StaffBiz;
import com.opc.freshness.service.integration.ProductService;
import com.opc.freshness.service.integration.ShopService;
import com.wormpex.cvs.product.api.bean.BeeProductDetail;
import com.wormpex.cvs.product.api.bean.BeeShop;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.jvm.hotspot.utilities.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by qishang on 2017/7/12.
 */
@RestController
public class ShopController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(ShopController.class);
    @Autowired
    private BatchBiz batchBiz;
    @Autowired
    private KindBiz kindBiz;
    @Autowired
    private StaffBiz staffBiz;
    @Autowired
    private ShopService shopService;
    @Autowired
    private ProductService productService;

    /**
     * 设备与门店定位
     *
     * @param deviceId 设备ID
     * @return
     */
    @RequestMapping(value = "/api/shop/position/v1", method = RequestMethod.GET)
    public Success postitionByDeviceId(@RequestParam String deviceId) {
        BeeShop shop = shopService.queryByDevice(deviceId);
        List<KindPo> kinds = kindBiz.selectListByDeviceId(deviceId);
        return new Success(
                DeviceVo.builder()
                        .shopInfo(
                                ShopVo.builder()
                                        .shopId(shop.getShopId())
                                        .shopName(shop.getPropInfo().getDisplayName())
                                        .build())
                        .categories(BeanCopyUtils.convertList(kinds, KindVo.class))
                        .build());
    }

    /**
     * 获取店员信息
     *
     * @param cardCode 卡号
     * @return
     */
    @RequestMapping(value = "/api/shop/staff/{cardCode}/v1", method = RequestMethod.GET)
    public Result<StaffVo> getStaff(@PathVariable String cardCode) {
        return new Success(staffBiz.selectByStaffCode(cardCode));
    }

    /**
     * 门店待废弃列表
     *
     * @param shopId
     * @return
     */
    @RequestMapping(value = "/api/shop/expire/list/v1", method = {RequestMethod.GET})
    public Result<List<ToAbortBatchVo>> getAbortList(@RequestParam Integer shopId) {
        return new Success(
                batchBiz.selectAbortList(shopId).stream()
                        .map(batchPo ->
                                ToAbortBatchVo
                                        .builder()
                                        .batchId(batchPo.getId())
                                        .batchName(batchPo.getName())
                                        .categoryId(batchPo.getKindsId())
                                        .quanity(batchPo.getTotalCount() - batchPo.getBreakCount() - batchPo.getExpiredCount())
                                        .expiredTime(batchPo.getExpiredTime())
                                        .build())
                        .collect(Collectors.toList()));
    }

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
        return new Success<>(kindBiz.selectSkuByBarCode(barCode, shopId));
    }

    /**
     * 设置sku关联品类
     *
     * @param skuKindDto
     * @return
     */
    @RequestMapping(value = "/api/shop/sku/setkinds/v1", method = RequestMethod.POST)
    public Result setkind(@RequestBody SkuKindDto skuKindDto) {
        Asserts.notNull(skuKindDto.getSkuId(), "skuId");
        Asserts.notNull(skuKindDto.getShopId(), "门店Id");
        Asserts.notNull(skuKindDto.getCategoryIds(), "skuId");
        for (Integer categoryId : skuKindDto.getCategoryIds()) {
            Asserts.notNull(categoryId, "品类Id");
        }
        return new Success(kindBiz.setkind(skuKindDto));
    }

    /**
     * 操作批次
     *
     * @param batchDto
     * @return
     */
    @RequestMapping(value = "/api/shop/sku/option/v1", method = {RequestMethod.POST})
    public Result<Boolean> addSku(@RequestBody BatchDto batchDto) {
        Asserts.notNull(batchDto.getShopId(), "店铺Id");
        Asserts.notNull(batchDto.getCategoryId(), "操作类型");
        Asserts.notNull(batchDto.getCreateTime(), "操作时间");

        Asserts.notEmpty(batchDto.getOperator(), "操作员");
        CollectionUtils.notEmpty(batchDto.getSkuList(), "sku列表");
        batchDto.getSkuList().forEach(skuDto -> {
            Asserts.notNull(skuDto.getSkuId(), "skuId");
            Asserts.notNull(skuDto.getQuantity(), "sku数量");
        });
        switch (OperateType.getByValue(batchDto.getOption())) {
            case MAKE: //制作
                Asserts.notEmpty(batchDto.getCategoryId(), "分类Id");
                return new Success<>(batchBiz.addBatch(batchDto));
            case LOSS: //报损
                Asserts.notNull(batchDto.getBatchId(), "批次号");
                return new Success<>(batchBiz.batchLoss(batchDto));
            case ABORT: //废弃
                Asserts.notNull(batchDto.getBatchId(), "批次号");
                return new Success<>(batchBiz.batchAbort(batchDto));
            default:
                return new Error<>("不支持的操作");
        }
    }

    /**
     * 获取店铺流水表
     *
     * @param shopId
     * @param type
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/api/shop/log/list/v1", method = {RequestMethod.GET})
    public Result<Pager<BatchLogVo>> getDetailPage(@RequestParam Integer shopId,
                                                   @RequestParam Integer type,
                                                   @RequestParam Integer pageNo,
                                                   @RequestParam Integer pageSize) {

        return new Success(batchBiz.selectLogByPage(shopId, OperateType.getByValue(type).getStatusList(), pageNo, pageSize))
                ;
    }

    /**
     * 操作类型
     */
    public enum OperateType {
        ALL(0, "全部", Lists.newArrayList(BatchPo.status.PREING, BatchPo.status.SALING, BatchPo.status.LOSS, BatchPo.status.ABORTED)),
        MAKE(1, "制作", Lists.newArrayList(BatchPo.status.PREING, BatchPo.status.SALING)),
        LOSS(2, "报损", Lists.newArrayList(BatchPo.status.LOSS)),
        ABORT(3, "废弃", Lists.newArrayList(BatchPo.status.ABORTED));
        private int value;
        private String desc;
        private List statusList;

        OperateType(int value, String desc, List<Integer> statusList) {
            this.value = value;
            this.desc = desc;
            this.statusList = statusList;
        }

        public int getValue() {
            return value;
        }

        public String getDesc() {
            return desc;
        }

        public List getStatusList() {
            return statusList;
        }

        public static OperateType getByValue(int value) {
            for (OperateType type : OperateType.values()) {
                if (type.value == value) {
                    return type;
                }
            }
            return null;
        }
    }
}
