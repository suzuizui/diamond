package com.opc.freshness.service.impl;

import com.opc.freshness.common.util.BeanCopyUtils;
import com.opc.freshness.common.util.DateUtils;
import com.opc.freshness.common.util.Pager;
import com.opc.freshness.domain.bo.BatchBo;
import com.opc.freshness.domain.bo.SkuBo;
import com.opc.freshness.domain.po.BatchPo;
import com.opc.freshness.domain.po.BatchPoExtras;
import com.opc.freshness.domain.po.BatchStatePo;
import com.opc.freshness.domain.po.KindPo;
import com.opc.freshness.domain.vo.BatchLogVo;
import com.opc.freshness.service.BatchService;
import com.opc.freshness.service.biz.BatchBiz;
import com.opc.freshness.service.biz.KindBiz;
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

    @Override
    public List<BatchPo> selectAbortList(Integer shopId) {
        BatchPo search = new BatchPo();
        search.setStatus(BatchPo.status.TO_ABORT);
        search.setShopId(shopId);
        return batchBiz.selectAbortList(shopId);
    }

    @Override
    public Pager<BatchLogVo> selectLogByPage(Integer shopId, List<Integer> statusList, Integer pageNo,
                                             Integer pageSize) {
        return batchBiz.selectLogByPage(shopId, statusList, pageNo, pageSize);
    }

    @Transactional
    public boolean addBatch(final BatchBo batchBo) {
        logger.info("addBatch dto:{}", batchBo.toString());
        // 查询大类
        KindPo kind = kindBiz.selectByPrimaryKey(batchBo.getCategoryId());
        // 查询门店
        BeeShop shop = shopService.queryById(batchBo.getShopId());
        // 封装批次
        BatchPo batch = BeanCopyUtils.convertClass(batchBo, BatchPo.class);
        batch.setShopName(shop.getPropInfo().getDisplayName());
        // 制作时间精确到分钟
        batch.setCreateTime(DateUtils.formatToMin(batchBo.getCreateTime()));
        // 预计过期时间 = 制作时间+延迟时间+过期时间
        batch.setExpiredTime(
                new Date(DateUtils.addMin(batch.getCreateTime(), kind.getDelay() + kind.getExpired().intValue())));
        // 设置状态
        batch.setStatus(BatchPo.status.MAKING);
        // 设置总个数，并插入流水表
        batch.setTotalCount(addBatchStateLog(batch, batchBo, batch.getStatus()));
        // 设置拓展字段
        batch.setExtras(JsonUtil.toJson(BatchPoExtras.builder().degree(batchBo.getDegree()).tag(batchBo.getTag())
                .unit(batchBo.getUnit()).build()));
        //设置分组标志
        if (batchBo.getSkuList().size() == 1) {
            batch.setGroupFlag(batchBo.getSkuList().get(0).getSkuId());
        }
        // 插入批次
        batchBiz.insertSelective(batch);
        return true;

    }

    @Override
    @Transactional
    public boolean batchLoss(BatchBo batchBo) {
        BatchPo batchPo = batchBiz.selectByPrimaryKey(batchBo.getBatchId());
        batchPo.setBreakCount(batchPo.getBreakCount() + addBatchStateLog(batchPo, batchBo, BatchPo.status.LOSS));
        batchBiz.updateBatchByPrimaryKeyLock(batchPo);
        return true;
    }

    @Override
    @Transactional
    public boolean batchAbort(BatchBo batchBo) {
        BatchPo batchPo = batchBiz.selectByPrimaryKey(batchBo.getBatchId());
        if (batchPo.getStatus() != BatchPo.status.TO_ABORT) {
            throw new BizException("批次状态不为待废弃");
        }
        batchPo.setStatus(BatchPo.status.ABORTED);
        batchPo.setBreakCount(batchPo.getBreakCount() + addBatchStateLog(batchPo, batchBo, batchPo.getStatus()));
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
    private int addBatchStateLog(BatchPo batch, final BatchBo batchBo, int stauts) {
        // log集合
        List<BatchStatePo> logs = new ArrayList<>(batchBo.getSkuList().size());
        // skuId集合
        Set<Integer> skuSet = batchBo.getSkuList().stream().map(SkuBo::getSkuId).collect(Collectors.toSet());

        Map<Integer, BeeProduct> skuMap = productService.queryProductMap(batch.getShopId(), skuSet);
        Map<Integer, BeeShopProduct> shopSkuMap = productService.queryShopProductMap(batch.getShopId(), skuSet);
        int totalQuantity = 0;

        for (SkuBo skuBo : batchBo.getSkuList()) {
            BatchStatePo state = new BatchStatePo();
            state.setBatchId(batch.getId());
            state.setStatus(stauts);
            state.setCreateTime(batch.getCreateTime());

            BeeProduct sku = skuMap.get(skuBo.getSkuId());
            BeeShopProduct shopSku = shopSkuMap.get(skuBo.getSkuId());

            state.setSkuId(sku.getId());
            state.setSkuStock(shopSku.getSaleCount());
            state.setSkuName(sku.getPropInfo().getDisplayName());
            state.setImgUrl(sku.getImages().stream().findFirst().get().getImageUrl());

            state.setOperator(batchBo.getOperator());

            state.setQuantity(skuBo.getQuantity());
            logs.add(state);
            totalQuantity += skuBo.getQuantity();
            if (stauts == BatchPo.status.MAKING) {
                if (StringUtils.isBlank(batch.getName())) {
                    batch.setName(state.getSkuName());
                } else {
                    batch.setName(batch.getName() + "、" + state.getSkuName() + "..");
                }
            }
        }
        batchBiz.batchInsertLog(logs);
        return totalQuantity;
    }
}
