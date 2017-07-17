package com.opc.freshness.service.biz.impl;

import com.opc.freshness.common.util.BeanCopyUtils;
import com.opc.freshness.common.util.DateUtils;
import com.opc.freshness.domain.dto.BatchDto;
import com.opc.freshness.domain.po.BatchPo;
import com.opc.freshness.domain.po.BatchStatePo;
import com.opc.freshness.domain.po.SkuKindsPo;
import com.opc.freshness.service.biz.BatchBiz;
import com.opc.freshness.service.dao.BatchMapper;
import com.opc.freshness.service.dao.BatchStateMapper;
import com.opc.freshness.service.dao.SkuKindsMapper;
import com.opc.freshness.service.integration.ProductService;
import com.opc.freshness.service.integration.ShopService;
import com.wormpex.biz.BizException;
import com.wormpex.cvs.product.api.bean.BeeProduct;
import com.wormpex.cvs.product.api.bean.BeeShop;
import com.wormpex.cvs.product.api.bean.BeeShopProduct;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by qishang on 2017/7/12.
 */
@Service
public class BatchBizImpl implements BatchBiz {
    private final static Logger logger = LoggerFactory.getLogger(BatchBizImpl.class);
    @Resource
    private ShopService shopService;
    @Resource
    private ProductService productService;
    @Resource
    private BatchMapper batchMapper;
    @Resource
    private BatchStateMapper batchStateMapper;
    @Resource
    private SkuKindsMapper skuKindsMapper;

    @Transactional
    public boolean addBatch(BatchDto batchDto) {
        logger.info("addBatch dto:{}", batchDto.toString());
        //查询大类
        SkuKindsPo kind = skuKindsMapper.selectByCode(batchDto.getKindCode());
        //查询门店
        BeeShop shop = shopService.queryById(batchDto.getShopId());
        //封装批次
        BatchPo batch = BeanCopyUtils.convertClass(batchDto, BatchPo.class);
        batch.setShopName(shop.getPropInfo().getDisplayName());
        //制作时间精确到分钟
        batch.setCreateTime(DateUtils.formatToMin(batchDto.getCreateTime()));
        //预计过期时间 = 制作时间+延迟时间+过期时间
        batch.setExpiredTime(
                new Date(
                        DateUtils.addMin(
                                batch.getCreateTime(),
                                kind.getDelay().intValue() + kind.getExpired().intValue()
                        )
                )
        );
        //设置状态
        if (kind.getDelay() <= 0) {
            //如果大类有延迟时间，则进入准备中
            batch.setStatus(BatchPo.status.PREING);
        } else {
            //如果没有延迟时间，直接进入售卖中
            batch.setStatus(BatchPo.status.SALING);
        }
        batch.setTotalCount(addBatchStateLog(batch, batchDto, batch.getStatus()));
        //插入批次
        batchMapper.insertSelective(batch);
        return true;

    }

    @Override
    @Transactional
    public boolean batchLoss(BatchDto batchDto) {
        BatchPo batchPo = batchMapper.selectByPrimaryKey(batchDto.getBatchId());
        batchPo.setBreakCount(batchPo.getBreakCount().intValue() + addBatchStateLog(batchPo, batchDto, BatchPo.status.LOSS));
        updateBatchByPrimaryKeyLock(batchPo);
        return false;
    }

    @Override
    @Transactional
    public boolean batchAbort(BatchDto batchDto) {
        BatchPo batchPo = batchMapper.selectByPrimaryKey(batchDto.getBatchId());
        batchPo.setStatus(BatchPo.status.ABORTED);
        batchPo.setBreakCount(batchPo.getBreakCount().intValue() + addBatchStateLog(batchPo, batchDto, batchPo.getStatus()));
        updateBatchByPrimaryKeyLock(batchPo);
        return false;
    }

    /**
     * 插入流水 必须在事务环境中
     *
     * @param batch
     * @param batchDto
     * @param stauts   * @see BatchPo.status
     * @return 所有记录中的quantity的和
     */
    @Transactional(propagation = Propagation.MANDATORY)
    private int addBatchStateLog(BatchPo batch, BatchDto batchDto, int stauts) {
        //log集合
        List<BatchStatePo> logs = new ArrayList<>(batchDto.getSkuList().size());
        //skuId集合
        Set<Integer> skuSet = batchDto.getSkuList().stream().map(skuDto -> skuDto.getSkuId()).collect(Collectors.toSet());

        Map<Integer, BeeProduct> skuMap = productService.queryProductMap(batch.getShopId(), skuSet);
        Map<Integer, BeeShopProduct> shopSkuMap = productService.queryShopProductMap(batch.getShopId(), skuSet);
        AtomicInteger totalQuantity = new AtomicInteger(0);

        batchDto.getSkuList().forEach(skuDto -> {
            BatchStatePo state = new BatchStatePo();
            state.setBatchId(batch.getId());
            state.setStatus(stauts);
            state.setCreateTime(batch.getCreateTime());

            BeeProduct sku = skuMap.get(skuDto.getSkuId());
            BeeShopProduct shopSku = shopSkuMap.get(skuDto.getSkuId());

            state.setSkuId(sku.getId());
            state.setSkuStock(shopSku.getSaleCount());
            state.setSkuName(sku.getPropInfo().getDisplayName());
            state.setImgUrl(sku.getImages().stream().findFirst().get().getImageUrl());

            state.setDegree(batchDto.getDegree());
            state.setTag(batchDto.getTag());
            state.setUnit(batchDto.getUnit());
            state.setOperator(batchDto.getOperator());

            state.setQuantity(skuDto.getQuantity());
            logs.add(state);
            totalQuantity.getAndAdd(skuDto.getQuantity());
        });

        batchStateMapper.batchInsert(logs);

        return totalQuantity.get();
    }

    /**
     * batch 具有乐观锁的更新
     *
     * @param batchPo
     */
    @Transactional
    private void updateBatchByPrimaryKeyLock(BatchPo batchPo) {
        Asserts.notNull(batchPo.getLastModifyTime(), "时间戳");
        int result = batchMapper.updateByPrimaryKeySelective(batchPo);
        if (result != 1) {
            throw new BizException("更新失败，请稍后再试");
        }
    }
}
