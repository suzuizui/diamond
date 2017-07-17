package com.opc.freshness.service.biz.impl;

import com.ctc.wstx.util.StringUtil;
import com.opc.freshness.common.util.BeanCopyUtils;
import com.opc.freshness.common.util.DateUtils;
import com.opc.freshness.common.util.PageRequest;
import com.opc.freshness.common.util.Pager;
import com.opc.freshness.domain.dto.BatchDto;
import com.opc.freshness.domain.dto.SkuDto;
import com.opc.freshness.domain.po.BatchPo;
import com.opc.freshness.domain.po.BatchPoExtras;
import com.opc.freshness.domain.po.BatchStatePo;
import com.opc.freshness.domain.po.KindPo;
import com.opc.freshness.domain.vo.BatchLogVo;
import com.opc.freshness.service.biz.BatchBiz;
import com.opc.freshness.service.dao.BatchMapper;
import com.opc.freshness.service.dao.BatchStateMapper;
import com.opc.freshness.service.dao.KindMapper;
import com.opc.freshness.service.integration.ProductService;
import com.opc.freshness.service.integration.ShopService;
import com.wormpex.api.json.JsonUtil;
import com.wormpex.biz.BizException;
import com.wormpex.cvs.product.api.bean.BeeProduct;
import com.wormpex.cvs.product.api.bean.BeeShop;
import com.wormpex.cvs.product.api.bean.BeeShopProduct;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.swing.text.TabableView;
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
    private KindMapper kindMapper;

    @Override
    public List<BatchPo> selectAbortList(Integer shopId) {
        BatchPo search = new BatchPo();
        search.setStatus(BatchPo.status.TO_ABORT);
        search.setShopId(shopId);
        return batchMapper.selectByRecord(search);
    }

    @Override
    public List<BatchPo> selectByRecord(BatchPo batchPo) {
        return batchMapper.selectByRecord(batchPo);
    }

    @Override
    public Pager<BatchLogVo> selectLogByPage(Integer shopId, List statusList, Integer pageNo, Integer pageSize) {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(new PageRequest.Page(pageNo, pageSize));
        Pager.PageData pageData = new Pager.PageData(pageNo, pageSize, batchStateMapper.selectVoCount(shopId, statusList));
        return new Pager<>(pageData, batchStateMapper.selectVoList(shopId, statusList, pageRequest));
    }

    @Transactional
    public boolean addBatch(final BatchDto batchDto) {
        logger.info("addBatch dto:{}", batchDto.toString());
        //查询大类
        KindPo kind = kindMapper.selectByCode(batchDto.getCategoryId());
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
        //设置总个数，并插入流水表
        batch.setTotalCount(addBatchStateLog(batch, batchDto, batch.getStatus()));
        //设置拓展字段
        batch.setExtras(
                JsonUtil.toJson(
                        BatchPoExtras.builder()
                                .degree(batchDto.getDegree())
                                .tag(batchDto.getTag())
                                .unit(batchDto.getUnit())
                                .build()));
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
        return true;
    }

    @Override
    @Transactional
    public boolean batchAbort(BatchDto batchDto) {
        BatchPo batchPo = batchMapper.selectByPrimaryKey(batchDto.getBatchId());
        if (batchPo.getStatus() != BatchPo.status.TO_ABORT) {
            throw new BizException("批次状态不为待废弃");
        }
        batchPo.setStatus(BatchPo.status.ABORTED);
        batchPo.setBreakCount(batchPo.getBreakCount().intValue() + addBatchStateLog(batchPo, batchDto, batchPo.getStatus()));
        updateBatchByPrimaryKeyLock(batchPo);
        return true;
    }

    /**
     * 批次更新  - 具有乐观锁的更新
     *
     * @param batchPo -lastModifyTime 乐观锁字段
     */
    @Override
    @Transactional
    public void updateBatchByPrimaryKeyLock(BatchPo batchPo) {
        Asserts.notNull(batchPo.getLastModifyTime(), "时间戳");
        int result = batchMapper.updateByPrimaryKeySelective(batchPo);
        if (result != 1) {
            throw new BizException("更新失败，请稍后再试");
        }
    }
    /* * * * * * * * * * * * * * * * * * * private method * * * * * * * * * * * * * * * * * * * */

    /**
     * 插入流水 必须在事务环境中
     *
     * @param batch
     * @param batchDto
     * @param stauts   * @see BatchPo.status
     * @return 所有记录中的quantity的和
     */
    @Transactional(propagation = Propagation.MANDATORY)
    private int addBatchStateLog(BatchPo batch, final BatchDto batchDto, int stauts) {
        //log集合
        List<BatchStatePo> logs = new ArrayList<>(batchDto.getSkuList().size());
        //skuId集合
        Set<Integer> skuSet = batchDto.getSkuList().stream().map(skuDto -> skuDto.getSkuId()).collect(Collectors.toSet());

        Map<Integer, BeeProduct> skuMap = productService.queryProductMap(batch.getShopId(), skuSet);
        Map<Integer, BeeShopProduct> shopSkuMap = productService.queryShopProductMap(batch.getShopId(), skuSet);
        int totalQuantity = 0;

        for (SkuDto skuDto : batchDto.getSkuList()) {
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

            state.setOperator(batchDto.getOperator());

            state.setQuantity(skuDto.getQuantity());
            logs.add(state);
            totalQuantity += skuDto.getQuantity();
            if (StringUtils.isBlank(batch.getName())) {
                batch.setName(state.getSkuName());
            } else {
                batch.setName(batch.getName() + "、" + state.getSkuName() + "..");
            }
        }

        batchStateMapper.batchInsert(logs);

        return totalQuantity;
    }

}
