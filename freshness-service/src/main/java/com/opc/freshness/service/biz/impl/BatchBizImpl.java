package com.opc.freshness.service.biz.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.opc.freshness.common.util.BeanCopyUtils;
import com.opc.freshness.common.util.DateUtils;
import com.opc.freshness.domain.dto.AddSkuDto;
import com.opc.freshness.domain.dto.SkuDto;
import com.opc.freshness.domain.po.BatchPo;
import com.opc.freshness.domain.po.BatchStatePo;
import com.opc.freshness.domain.po.SkuKindsPo;
import com.opc.freshness.service.biz.BatchBiz;
import com.opc.freshness.service.biz.SkuKindBiz;
import com.opc.freshness.service.dao.BatchMapper;
import com.opc.freshness.service.dao.BatchStateMapper;
import com.opc.freshness.service.dao.SkuKindsMapper;
import com.opc.freshness.service.integration.ProductService;
import com.opc.freshness.service.integration.ShopService;
import com.wormpex.cvs.product.api.bean.BeeProduct;
import com.wormpex.cvs.product.api.bean.BeeShop;
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
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(BatchBizImpl.class);
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
    public boolean addBatch(AddSkuDto addSkuDto) {
        logger.info("addBatch dto:{}", addSkuDto.toString());
        //查询大类
        SkuKindsPo kind = skuKindsMapper.selectByCode(addSkuDto.getKindCode());
        //查询门店
        BeeShop shop = shopService.queryById(addSkuDto.getShopId());
        //封装批次
        BatchPo batch = BeanCopyUtils.convertClass(addSkuDto, BatchPo.class);
        batch.setShopName(shop.getPropInfo().getDisplayName());
        //制作时间精确到分钟
        batch.setCreateTime(DateUtils.formatToMin(addSkuDto.getCreateTime()));
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
        //
        List<BatchStatePo> logs = new ArrayList<>(addSkuDto.getSkuList().size());
        Set<Integer> skuSet = addSkuDto.getSkuList().stream().map(skuDto -> skuDto.getSkuId()).collect(Collectors.toSet());
        Map<Integer, BeeProduct> skuMap = productService.queryProductMap(shop.getShopId(), skuSet);

        addSkuDto.getSkuList().forEach(skuDto -> {
            BatchStatePo state = new BatchStatePo();
            state.setBatchId(batch.getId());
            state.setStatus(batch.getStatus());
            state.setCreateTime(batch.getCreateTime());

            BeeProduct product = skuMap.get(skuDto.getSkuId());
            state.setSkuId(product.getId());
//            state.setSkuStock(product.get);
//            state.setSkuName();
//            state.setImgUrl();

            state.setDegree(addSkuDto.getDegree());
            state.setTag(addSkuDto.getTag());
            state.setUnit(addSkuDto.getUnit());
            logs.add(state);
        });

        return true;

    }

    public int addBatchLog(BatchStatePo batchState) {
        return batchStateMapper.insertSelective(batchState);
    }
}
