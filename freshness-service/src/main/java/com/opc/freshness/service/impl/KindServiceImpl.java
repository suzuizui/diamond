package com.opc.freshness.service.impl;

import com.google.common.collect.Maps;
import com.opc.freshness.domain.bo.*;
import com.opc.freshness.domain.po.BatchPo;
import com.opc.freshness.domain.po.KindPo;
import com.opc.freshness.domain.po.SkuKindPo;
import com.opc.freshness.domain.vo.PeakVo;
import com.opc.freshness.domain.vo.SkuVo;
import com.opc.freshness.enums.PeakEnums;
import com.opc.freshness.service.KindService;
import com.opc.freshness.service.biz.BatchBiz;
import com.opc.freshness.service.biz.KindBiz;
import com.opc.freshness.service.integration.ProductService;
import com.wormpex.cvs.product.api.bean.BeeProductDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AUTHOR: qishang
 * DATE:2017/7/17.
 */
@Service
public class KindServiceImpl implements KindService {
    private final static Logger logger = LoggerFactory.getLogger(KindServiceImpl.class);

    @Resource
    private KindBiz kindBiz;
    @Resource
    private BatchBiz batchBiz;
    @Resource
    private ProductService productService;

    @Override
    public KindPo selectByPrimaryKey(Integer id) {
        return kindBiz.selectByPrimaryKey(id);
    }

    @Override
    public List<KindPo> selectListByDeviceId(String deviceId) {
        return kindBiz.selectListByDeviceId(deviceId);
    }

    @Override
    public Boolean setkind(SkuKindBo bo) {
        BeeProductDetail sku = productService.queryProductDetail(bo.getSkuId());
        List<SkuKindPo> skuKindList =
                bo.getCategoryIds().stream().map(kindId -> {
                    SkuKindPo po = new SkuKindPo();
                    po.setSkuId(bo.getSkuId());
                    po.setShopId(bo.getShopId());
                    po.setSkuName(sku.getProductBase().getDisplayName());
                    po.setImgUrl(sku.getProductBase().getImage());
                    po.setKindId(kindId);
                    return po;
                }).collect(Collectors.toList());
        kindBiz.batchInsertSkuKinds(skuKindList);
        return true;
    }

    @Override
    public List<SkuVo> selectSkuList(Integer shopId, Integer categoryId) throws ParseException {
        Date now = new Date();
        final PeakEnums enums = PeakEnums.getByDate(now);
        if (enums == null) {
            return kindBiz.selectSkuList(shopId, categoryId)
                    .stream().map(skuKindPo ->
                            SkuVo.builder()
                                    .skuId(skuKindPo.getSkuId())
                                    .skuName(skuKindPo.getSkuName())
                                    .imgUrl(skuKindPo.getImgUrl())
                                    .build())
                    .collect(Collectors.toList());
        } else {
            final Map<Integer, SkuPeakBo> map = kindBiz.selectSkuMakeList(shopId, categoryId, enums.getBeginDate(now), enums.getEndDate(now));
            List<SkuPeakBo> peakBos = kindBiz.selectSkuListByPeak(shopId, categoryId, enums.getId(), now);
            return peakBos.stream()
                    .map(bo -> {
                        logger.info("bo:{}", bo.getSkuId());
                        return SkuVo.builder()
                                .skuId(bo.getSkuId())
                                .skuName(bo.getSkuName())
                                .imgUrl(bo.getImgUrl())
                                .peakInfo(PeakVo.builder()
                                        .id(enums.getId())
                                        .name(enums.getName())
                                        .makeCount(map.get(bo.getSkuId()) != null && map.get(bo.getSkuId()).getCount() != null ? map.get(bo.getSkuId()).getCount() : 0)
                                        .adviseCount(bo.getAdviceCount())
                                        .build())
                                .build();
                    })
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<SkuMakeBo> skuMakeInfoList(Integer shopId, Integer categoryId, Date date) {
        logger.info("skuMakeInfoList shopId:{} categoryId:{} date:{}", shopId, categoryId, date);
        List<SkuCountBo> makeList = batchBiz.selectSkuCountByStatus(shopId, categoryId, date, BatchPo.status.MAKING);
        List<SkuCountBo> lossList = batchBiz.selectSkuCountByStatus(shopId, categoryId, date, BatchPo.status.LOSS);
        List<SkuCountBo> abortList = batchBiz.selectSkuCountByStatus(shopId, categoryId, date, BatchPo.status.ABORTED);

        HashMap<Integer, SkuMakeBo> boMap = Maps.newHashMapWithExpectedSize(makeList.size());
        //制作列表
        makeList
                .forEach(makeBo ->
                        boMap.put(makeBo.getSkuId(), new SkuMakeBo(makeBo.getId(), makeBo.getSkuId(), makeBo.getSkuName(), makeBo.getCount()))
                );
        //报损列表
        lossList
                .forEach(lossBo -> {
                    SkuMakeBo makeBo = boMap.get(lossBo.getSkuId());
                    if (makeBo != null) {
                        makeBo.setLossCount(lossBo.getCount());
                    }
                });
        //废弃列表
        abortList.forEach(
                abortBo -> {
                    SkuMakeBo makeBo = boMap.get(abortBo.getSkuId());
                    if (makeBo != null) {
                        makeBo.setAbortCount(abortBo.getCount());
                    }
                });
        //根据SkuId排序
        return boMap
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    /**
     * 获得某个品类下sku流水信息
     *
     * @param shopId
     * @param categoryId
     * @param date
     * @return
     */
    @Override
    public List<SkuDetailBo> skuDetailInfoList(Integer shopId, Integer categoryId, Date date) {
        logger.info("skuDetailInfoList shopId:{} categoryId:{} date:{}", shopId, categoryId, date);
        return batchBiz.skuDetailInfoList(shopId, categoryId, date);
    }

    public List<SkuDetailBo> skuDetailInfoListByBatchId(Integer batchId) {
        logger.info("skuDetailInfoListByBatchId  batchId:{}", batchId);
        return batchBiz.skuDetailInfoListByBatchId(batchId);
    }
}
