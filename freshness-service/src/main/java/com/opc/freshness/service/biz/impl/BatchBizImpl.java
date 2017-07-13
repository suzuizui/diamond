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
import com.wormpex.cvs.product.api.bean.BeeProduct;
import com.wormpex.cvs.product.api.bean.BeeShop;
import com.wormpex.cvs.product.api.bean.BeeShopProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
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
//        预计过期时间 = 制作时间+延迟时间+过期时间
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
            batch.setStatus(BatchPo.status.ZBZ);
        } else {
            //如果没有延迟时间，直接进入售卖中
            batch.setStatus(BatchPo.status.SMZ);
        }
        //插入批次
        batchMapper.insertSelective(batch);

        //插入流水
        List<BatchStatePo> logs = new ArrayList<>(batchDto.getSkuList().size());
        Set<Integer> skuSet = batchDto.getSkuList().stream().map(skuDto -> skuDto.getSkuId()).collect(Collectors.toSet());
        Map<Integer, BeeProduct> skuMap = productService.queryProductMap(shop.getShopId(), skuSet);
        Map<Integer, BeeShopProduct> shopSkuMap = productService.queryShopProductMap(shop.getShopId(), skuSet);

        batchDto.getSkuList().forEach(skuDto -> {
            BatchStatePo state = new BatchStatePo();
            state.setBatchId(batch.getId());
            state.setStatus(batch.getStatus());
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
        });
        batchStateMapper.batchInsert(logs);
        return true;

    }
}
