package com.opc.freshness.controller;

import com.google.common.collect.Lists;
import com.opc.freshness.api.model.dto.BatchDto;
import com.opc.freshness.api.model.dto.SkuKindDto;
import com.opc.freshness.common.Error;
import com.opc.freshness.common.Result;
import com.opc.freshness.common.Success;
import com.opc.freshness.common.util.BeanCopyUtils;
import com.opc.freshness.common.util.CollectionUtils;
import com.opc.freshness.common.util.Pager;
import com.opc.freshness.domain.bo.BatchBo;
import com.opc.freshness.domain.bo.SkuBo;
import com.opc.freshness.domain.bo.SkuKindBo;
import com.opc.freshness.domain.po.BatchPo;
import com.opc.freshness.domain.po.KindPo;
import com.opc.freshness.domain.vo.*;
import com.opc.freshness.service.BatchService;
import com.opc.freshness.service.KindService;
import com.opc.freshness.service.StaffService;
import com.opc.freshness.service.integration.ShopService;
import com.wormpex.cvs.product.api.bean.BeeShop;
import com.wormpex.inf.wmq.utils.JsonUtils;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.opc.freshness.controller.ShopController.OperateType.getByValue;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/12.
 */
@RestController
public class ShopController {
    private final static Logger logger = LoggerFactory.getLogger(ShopController.class);

    @Autowired
    private BatchService batchService;
    @Autowired
    private KindService kindService;
    @Autowired
    private StaffService staffService;
    @Autowired
    private ShopService shopService;

    /**
     * 设备与门店定位
     *
     * @param deviceId 设备ID
     * @return
     */
    @RequestMapping(value = "/api/shop/position/v1", method = RequestMethod.GET)
    public @ResponseBody
    Result<DeviceVo> postitionByDeviceId(@RequestParam String deviceId) {
        logger.info("postitionByDeviceId deviceId:{}", deviceId);
        BeeShop shop = shopService.queryByDevice(deviceId);
        List<KindPo> kinds = kindService.selectListByDeviceId(deviceId);
        return new Success<DeviceVo>(
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
        return new Success<StaffVo>(staffService.selectByCardCode(cardCode));
    }

    /**
     * 获取大类下sku列表
     *
     * @param shopId     门店编号
     * @param categoryId 品类编号
     * @return
     */
    @RequestMapping(value = "/api/shop/sku/list/v1", method = RequestMethod.GET)
    public Result<List<SkuVo>> getSkuList(@RequestParam Integer shopId,
                                          @RequestParam Integer categoryId) {
        return new Success<List<SkuVo>>(kindService.selectSkuList(shopId, categoryId));
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
        return new Success<SkuVo>(kindService.selectSkuByBarCode(barCode, shopId));
    }

    /**
     * 门店制作中和待废弃列表
     *
     * @param shopId
     * @return
     */
    @RequestMapping(value = "/api/shop/expire/list/v1", method = {RequestMethod.GET})
    public Result<List<BatchVo>> getMakeAndAbortList(@RequestParam Integer shopId) {
        return new Success<List<BatchVo>>(
                batchService.selectMakeAndAbortList(shopId).stream()
                        .map(batchPo ->
                                BatchVo
                                        .builder()
                                        .batchId(batchPo.getId())
                                        .batchName(batchPo.getName())
                                        .status(batchPo.getStatus())
                                        .categoryId(batchPo.getKindsId())
                                        .quanity(batchPo.getTotalCount() - batchPo.getBreakCount() - batchPo.getExpiredCount())
                                        .expiredTime(batchPo.getExpiredTime())
                                        .build())
                        .collect(Collectors.toList()));
    }


    /**
     * 设置sku关联品类
     *
     * @param skuKindDto
     * @return
     */
    @RequestMapping(value = "/api/shop/sku/setkinds/v1", method = RequestMethod.POST)
    public Result<Boolean> setkind(@RequestBody SkuKindDto skuKindDto) {
        Asserts.notNull(skuKindDto.getSkuId(), "skuId");
        Asserts.notNull(skuKindDto.getShopId(), "门店Id");
        Asserts.notNull(skuKindDto.getCategoryIds(), "skuId");
        for (Integer categoryId : skuKindDto.getCategoryIds()) {
            Asserts.notNull(categoryId, "品类Id");
        }
        return new Success<Boolean>(kindService.setkind(BeanCopyUtils.convertClass(skuKindDto, SkuKindBo.class)));
    }

    /**
     * 查询批次明细
     *
     * @return
     */
    @RequestMapping(value = "/api/shop/batch/detail/v1", method = RequestMethod.POST)
    public Result<BatchVo> batchDetail(@RequestParam Integer batchId) {
        return new Success<BatchVo>(batchService.skuDetailInfoListByBatchId(batchId));
    }

    /**
     * 操作批次
     *
     * @param batchDto
     * @return
     */
    @RequestMapping(value = "/api/shop/sku/option/v1", method = {RequestMethod.POST})
    public Result<Boolean> option(@RequestBody BatchDto batchDto) {

        Asserts.notNull(batchDto.getShopId(), "店铺Id");
        Asserts.notNull(batchDto.getCategoryId(), "操作类型");
        Asserts.notNull(batchDto.getCreateTime(), "操作时间");

        Asserts.notEmpty(batchDto.getOperator(), "操作员");

        CollectionUtils.notEmpty(batchDto.getSkuList(), "sku列表");
        batchDto.getSkuList().forEach(skuDto -> {
            Asserts.notNull(skuDto.getSkuId(), "skuId");
            Asserts.notNull(skuDto.getQuantity(), "sku数量");
        });
        BatchBo bo = BeanCopyUtils.convertClass(batchDto, BatchBo.class);
        bo.setSkuList(batchDto.getSkuList().stream().map(skuDto -> new SkuBo(skuDto.getSkuId(), skuDto.getQuantity())).collect(Collectors.toList()));
        switch (getByValue(batchDto.getOption())) {
            case MAKE: //制作
                Asserts.notNull(batchDto.getCategoryId(), "分类Id");
                return new Success<Boolean>(batchService.addBatch(bo));
            case LOSS: //报损
                Asserts.notNull(batchDto.getBatchId(), "批次号");
                return new Success<Boolean>(batchService.batchLoss(bo));
            case ABORT: //废弃
                Asserts.notNull(batchDto.getBatchId(), "批次号");
                return new Success<Boolean>(batchService.batchAbort(bo));
            default:
                return new Error<Boolean>("不支持的操作");
        }
    }

    /**
     * 获取店铺流水表
     *
     * @param shopId   门店Id
     * @param type     @see OperateType
     * @param pageNo   页码
     * @param pageSize 分页大小
     * @return
     */
    @RequestMapping(value = "/api/shop/log/list/v1", method = {RequestMethod.GET})
    public Result<Pager<BatchLogVo>> getDetailPage(@RequestParam Integer shopId,
                                                   @RequestParam Integer type,
                                                   @RequestParam Integer pageNo,
                                                   @RequestParam Integer pageSize) {

        return new Success<Pager<BatchLogVo>>(batchService.selectLogByPage(shopId, getByValue(type).getStatusList(), pageNo, pageSize));
    }

    /**
     * 操作类型
     */
    public enum OperateType {
        ALL(0, "全部", Lists.newArrayList(BatchPo.status.MAKING, BatchPo.status.LOSS, BatchPo.status.ABORTED)),
        MAKE(1, "制作", Lists.newArrayList(BatchPo.status.MAKING)),
        LOSS(2, "报损", Lists.newArrayList(BatchPo.status.LOSS)),
        ABORT(3, "废弃", Lists.newArrayList(BatchPo.status.ABORTED));
        private int value;
        private String desc;
        private List<Integer> statusList;

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

        public List<Integer> getStatusList() {
            return statusList;
        }

        public static OperateType getByValue(int value) {
            for (OperateType type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            return null;
        }
    }

    public static void main(String[] args) {
        BatchVo vo = BatchVo.builder()
                .batchId(1)
                .batchName("test")
                .categoryId(1)
                .status(1)
                .quanity(10)
                .expiredTime(new Date())
                .skuList(Lists.newArrayList(SkuVo.builder().skuId(1).skuName("test1").imgUrl("www.www.www").count(5).abortCount(5).build()))
                .build();
        System.out.println(JsonUtils.toJsonString(new Success<>(vo)));
    }
}
