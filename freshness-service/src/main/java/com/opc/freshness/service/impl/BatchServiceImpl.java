package com.opc.freshness.service.impl;

import com.opc.freshness.common.util.BeanCopyUtils;
import com.opc.freshness.common.util.DateUtils;
import com.opc.freshness.common.util.Pager;
import com.opc.freshness.domain.bo.BatchBo;
import com.opc.freshness.domain.bo.SkuBo;
import com.opc.freshness.domain.bo.SkuDetailBo;
import com.opc.freshness.domain.po.*;
import com.opc.freshness.domain.vo.BatchLogVo;
import com.opc.freshness.domain.vo.BatchVo;
import com.opc.freshness.domain.vo.SkuVo;
import com.opc.freshness.service.BatchService;
import com.opc.freshness.service.biz.BatchBiz;
import com.opc.freshness.service.biz.KindBiz;
import com.opc.freshness.service.biz.SkuBiz;
import com.opc.freshness.service.biz.impl.BatchBizImpl;
import com.opc.freshness.service.integration.ProductService;
import com.opc.freshness.service.integration.ShopService;
import com.wormpex.api.json.JsonUtil;
import com.wormpex.biz.BizException;
import com.wormpex.cvs.product.api.bean.BeeProduct;
import com.wormpex.cvs.product.api.bean.BeeShop;
import com.wormpex.cvs.product.api.bean.BeeShopProduct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AUTHOR: qishang DATE:2017/7/17.
 */
@Service
public class BatchServiceImpl implements BatchService {
    private final static Logger logger = LoggerFactory.getLogger(BatchBizImpl.class);
    @Resource
    private ShopService shopService;
    @Resource
    private ProductService productService;
    @Resource
    private BatchBiz batchBiz;
    @Resource
    private KindBiz kindBiz;
    @Resource
    private SkuBiz skuBiz;

    @Override
    public List<BatchPo> selectMakeAndAbortList(Integer shopId) {
        return batchBiz.selectMakeAndAbortList(shopId);
    }


    @Override
    public BatchPo selectByPrimaryKey(Integer batchId) {
        return batchBiz.selectByPrimaryKey(batchId);
    }

    @Override
    public BatchVo skuDetailInfoListByBatchId(Integer batchId) {
        BatchPo po = batchBiz.selectByPrimaryKey(batchId);
        if (po == null) {
            logger.info("skuDetailInfoListByBatchId 不存在的批次 batchId:{}", batchId);
            throw new BizException("不存在的批次");
        }
        List<SkuDetailBo> detailBos = batchBiz.skuDetailInfoListByBatchId(batchId);
        return BatchVo.builder()
                .batchId(batchId)
                .categoryId(po.getKindsId())
                .batchName(po.getName())
                .status(po.getStatus())
                .quanity(po.getTotalCount())
                .expiredTime(po.getExpiredTime())
                .skuList(detailBos.stream()
                        .map(bo ->
                                SkuVo.builder()
                                        .skuId(bo.getSkuId())
                                        .skuName(bo.getSkuName())
                                        .imgUrl(bo.getImgUrl())
                                        .count(bo.getTotalCount())
                                        .abortCount(bo.getExpiredCount())
                                        .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public List<BatchPo> batchListBySkuIdAndKindId(Integer skuId, Integer categoryId, Integer shopId, Integer limit) {

        return batchBiz.batchListBySkuIdAndKindId(skuId, categoryId, shopId, limit);
    }

    @Override
    public Pager<BatchLogVo> selectLogByPage(Integer shopId, List<Integer> statusList, Integer pageNo,
                                             Integer pageSize) {
        return batchBiz.selectLogByPage(shopId, statusList, pageNo, pageSize);
    }

    @Override
    public Date selectNextTime(Date now, Integer shopId) {
        return batchBiz.selectNextTime(now, shopId);
    }

    @Transactional
    public boolean addBatch(final BatchBo batchBo) {
        logger.info("addBatch dto:{}", batchBo.toString());
        // 查询大类
        KindPo kind = kindBiz.selectByPrimaryKey(batchBo.getCategoryId());
        // 查询门店
        BeeShop shop = shopService.queryById(batchBo.getShopId());
        if (shop == null) {
            throw new BizException("未查找到门店");
        }
        // 封装批次
        BatchPo batchPo = BeanCopyUtils.convertClass(batchBo, BatchPo.class);
        //设置品类
        batchPo.setKindsId(batchBo.getCategoryId());
        batchPo.setShopName(shop.getPropInfo().getDisplayName());
        // 制作时间精确到分钟
        batchPo.setCreateTime(DateUtils.formatToMin(batchBo.getCreateTime()));
        // 预计过期时间 = 制作时间+延迟时间+过期时间
        SkuTimePo skuTimePo = skuBiz.selectRuleBySkuIdAndKindId(batchBo.getSkuList().get(0).getSkuId(), batchBo.getCategoryId());
        if (batchBo.getSkuList().size() != 1 || skuTimePo == null) {
            //如果批次有多个sku或者没有sku特殊规则
            batchPo.setDelayTime(new Date(DateUtils.addMin(batchPo.getCreateTime(), kind.getDelay())));
            batchPo.setExpiredTime(new Date(DateUtils.addMin(batchPo.getCreateTime(), kind.getExpired().intValue())));
        } else {
            //如果有特殊规则
            batchPo.setDelayTime(new Date(DateUtils.addMin(batchPo.getCreateTime(), skuTimePo.getDelay())));
            batchPo.setExpiredTime(new Date(DateUtils.addMin(batchPo.getCreateTime(), skuTimePo.getExpired())));
        }
        // 设置状态
        batchPo.setStatus(BatchPo.status.MAKING);
        //设置鲜度标识
        batchPo.setFreshFlag((byte) 1);
        // 设置拓展字段
        batchPo.setExtras(JsonUtil.toJson(new BatchPoExtras(batchBo.getDegree(), batchBo.getTag(), batchBo.getUnit())));
        //设置分组标志
        if (batchBo.getSkuList().size() == 1 && batchBo.getCategoryId() != 4) {
            batchPo.setGroupFlag(batchBo.getSkuList().get(0).getSkuId());
        }
        //将以前同组批次置为旧批次
        BatchPo oldPo = new BatchPo();
        oldPo.setShopId(batchPo.getShopId());
        oldPo.setKindsId(batchBo.getCategoryId());
        oldPo.setGroupFlag(batchPo.getGroupFlag());
        oldPo.setFreshFlag((byte) 0);
        batchBiz.updateByGroupFlagSelective(oldPo);

        //插入批次，返回主键
        batchBiz.insertSelective(batchPo);
        // 设置总个数，并插入流水表
        batchPo.setTotalCount(addBatchStateLog(batchPo, batchBo, batchPo.getStatus()));

        if (batchPo.getDelayTime().compareTo(new Date()) <= 0) {
            batchPo.setStatus(BatchPo.status.SALING);
        }
        // 更新批次总个数
        batchBiz.updateByPrimaryKeySelective(batchPo);

        return true;

    }

    @Override
    @Transactional
    public boolean batchLoss(BatchBo batchBo) {
        BatchPo batchPo = batchBiz.selectByPrimaryKey(batchBo.getBatchId());
        if (batchPo.getStatus() == BatchPo.status.ABORTED) {
            throw new BizException("批次已经废弃 batchId:" + batchBo.getBatchId());
        } else if (batchPo.getStatus() == BatchPo.status.SELL_OUT) {
            throw new BizException("批次已售空 batchId:" + batchBo.getBatchId());
        }
        batchPo.setBreakCount(batchPo.getBreakCount() + addBatchStateLog(batchPo, batchBo, BatchPo.status.LOSS));
        batchBiz.updateBatchByPrimaryKeyLock(batchPo);
        return true;
    }

    @Override
    @Transactional
    public boolean batchAbort(BatchBo batchBo) {
        BatchPo batchPo = batchBiz.selectByPrimaryKey(batchBo.getBatchId());
        if (batchPo.getStatus() == BatchPo.status.ABORTED) {
            throw new BizException("批次已经废弃 batchId:" + batchBo.getBatchId());
        } else if (batchPo.getStatus() == BatchPo.status.SELL_OUT) {
            throw new BizException("批次已售空 batchId:" + batchBo.getBatchId());
        }
        batchPo.setStatus(BatchPo.status.ABORTED);
        batchPo.setExpiredRealTime(batchBo.getCreateTime());
        batchPo.setExpiredCount(batchPo.getExpiredCount() + addBatchStateLog(batchPo, batchBo, batchPo.getStatus()));
        batchBiz.updateBatchByPrimaryKeyLock(batchPo);
        return true;
    }

    @Override
    public boolean batchSellOut(BatchBo batchBo) {
        BatchPo batchPo = batchBiz.selectByPrimaryKey(batchBo.getBatchId());
        if (batchPo.getStatus() == BatchPo.status.ABORTED) {
            throw new BizException("批次已经废弃 batchId:" + batchBo.getBatchId());
        } else if (batchPo.getStatus() == BatchPo.status.SELL_OUT) {
            throw new BizException("批次已售空 batchId:" + batchBo.getBatchId());
        }
        batchPo.setStatus(BatchPo.status.SELL_OUT);
        batchPo.setSellOutTime(batchBo.getCreateTime());
        batchBiz.updateBatchByPrimaryKeyLock(batchPo);
        return true;
    }

    /* * * * * * * * * * * * * * * * * * * private method * * * * * * * * * * * * * * * * * * * */

    /**
     * 插入流水
     *
     * @param batch
     * @param batchBo
     * @param stauts  * @see BatchPo.status
     * @return 所有记录中的quantity的和
     */
    @Transactional
    int addBatchStateLog(BatchPo batch, final BatchBo batchBo, int stauts) {
        // log集合
        List<BatchStatePo> logs = new ArrayList<>(batchBo.getSkuList().size());
        // skuId集合
        Set<Integer> skuSet = batchBo.getSkuList().stream().map(SkuBo::getSkuId).collect(Collectors.toSet());

        Map<Integer, BeeProduct> skuMap = productService.queryProductMap(batch.getShopId(), skuSet);
        Map<Integer, BeeShopProduct> shopSkuMap = productService.queryShopProductMap(batch.getShopId(), skuSet);
        int totalQuantity = 0;
        final Date makeTime = new Date();
        for (SkuBo skuBo : batchBo.getSkuList()) {
            BatchStatePo state = new BatchStatePo();
            state.setBatchId(batch.getId());
            state.setShopId(batch.getShopId());
            state.setStatus(stauts);


            BeeProduct sku = skuMap.get(skuBo.getSkuId());
            BeeShopProduct shopSku = shopSkuMap.get(skuBo.getSkuId());

            state.setSkuId(sku.getId());
            state.setSkuCode(sku.getProductCode());
            state.setSkuStock(shopSku.getSaleCount());
            state.setSkuName(sku.getPropInfo().getDisplayName());
            state.setImgUrl(sku.getImages().isEmpty() ? "" : sku.getImages().get(0).getImageUrl());

            state.setOperator(batchBo.getOperator());
            state.setCreateTime(makeTime);

            state.setQuantity(skuBo.getQuantity());
            logs.add(state);
            totalQuantity += skuBo.getQuantity();
            if (stauts == BatchPo.status.MAKING) {
                if (StringUtils.isBlank(batch.getName())) {
                    batch.setName(state.getSkuName());
                } else {
                    batch.setName(batch.getName() + "、" + state.getSkuName());
                }
            }
        }
        batchBiz.batchInsertLog(logs);
        return totalQuantity;
    }
}
