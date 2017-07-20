package com.opc.freshness.service.biz.impl;

import com.opc.freshness.domain.bo.SkuPeakBo;
import com.opc.freshness.domain.po.KindPo;
import com.opc.freshness.domain.po.SkuKindPo;
import com.opc.freshness.domain.vo.KindVo;
import com.opc.freshness.service.biz.KindBiz;
import com.opc.freshness.service.dao.DeviceKindMapper;
import com.opc.freshness.service.dao.KindMapper;
import com.opc.freshness.service.dao.SkuKindMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/12
 */
@Service
public class KindBizImpl implements KindBiz {
    private final static Logger logger = LoggerFactory.getLogger(KindBizImpl.class);
    @Resource
    private KindMapper kindMapper;
    @Resource
    private SkuKindMapper skuKindMapper;
    @Resource
    private DeviceKindMapper deviceKindMapper;

    @Override
    public KindPo selectByPrimaryKey(Integer id) {
        logger.info("selectByPrimaryKey id:{}", id);
        return kindMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<KindPo> selectListByDeviceId(String deviceId) {
        return deviceKindMapper.selectKindListByDeviceId(deviceId);
    }

    @Override
    public List<KindVo> selectKindBySkuIdAndShopId(Integer skuId, Integer shopId) {
        return skuKindMapper.selectKind(skuId, shopId);
    }

    @Override
    public List<KindPo> selectAll() {
        return kindMapper.selectAll();
    }

    @Override
    public int batchInsertSkuKinds(List<SkuKindPo> skuKindList) {
        return skuKindMapper.batchInsert(skuKindList);
    }

    @Override
    public List<SkuKindPo> selectSkuList(Integer shopId, Integer kindId) {
        SkuKindPo po = new SkuKindPo();
        po.setShopId(shopId);
        po.setKindId(kindId);
        return skuKindMapper.selectByRecord(po);
    }

    @Override
    public List<SkuPeakBo> selectSkuListByPeak(Integer shopId, Integer kindId, Integer peakId, Date target) {
        return skuKindMapper.selectWithPeakInfo(shopId, kindId, peakId, target);
    }

    @Override
    public Map<Integer, SkuPeakBo> selectSkuMakeList(Integer shopId, Integer kindId, Date beginDate, Date endDate) {
        return skuKindMapper.selectSkuMakeList(shopId,kindId,beginDate,endDate);
    }
}
