package com.opc.freshness.service.biz.impl;

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
import java.util.List;

/**
 * Created by qishang on 2017/7/12.
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
    public List<KindVo> selectKindBySkuIdAndShopId(int skuId, Integer shopId) {
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
}
