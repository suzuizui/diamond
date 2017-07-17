package com.opc.freshness.service.biz.impl;

import com.opc.freshness.common.util.BeanCopyUtils;
import com.opc.freshness.common.util.CollectionUtils;
import com.opc.freshness.domain.dto.SkuKindDto;
import com.opc.freshness.domain.po.KindPo;
import com.opc.freshness.domain.po.SkuKindPo;
import com.opc.freshness.domain.vo.KindVo;
import com.opc.freshness.domain.vo.SkuVo;
import com.opc.freshness.service.biz.KindBiz;
import com.opc.freshness.service.dao.DeviceKindMapper;
import com.opc.freshness.service.dao.KindMapper;
import com.opc.freshness.service.dao.SkuKindMapper;
import com.opc.freshness.service.integration.ProductService;
import com.wormpex.cvs.product.api.bean.BeeProductDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by qishang on 2017/7/12.
 */
@Service
public class KindBizImpl implements KindBiz {
    private final static Logger logger = LoggerFactory.getLogger(KindBizImpl.class);
    @Resource
    private ProductService productService;
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
    public SkuVo selectSkuByBarCode(String barCode, Integer shopId) {
        Boolean hasCategory = true;
        BeeProductDetail product = productService.queryProductDetailByBarcode(barCode);
        List<KindVo> kindVoList = skuKindMapper.selectKind(product.getProductBase().getProductId(), shopId);
        if (CollectionUtils.isEmpty(kindVoList)) {
            hasCategory = false;
            kindVoList = BeanCopyUtils.convertList(kindMapper.selectAll(), KindVo.class);
        }
        return SkuVo.builder()
                .skuId(product.getProductBase().getProductId())
                .skuName(product.getProductBase().getDisplayName())
                .imgUrl(product.getProductBase().getImage())
                .categories(kindVoList)
                .hasCategory(hasCategory)
                .build();
    }

    @Override
    public Boolean setkind(final SkuKindDto dto) {
        List<SkuKindPo> skuKindList =
                dto.getCategoryIds().stream().map(kindId -> {
                    SkuKindPo po = new SkuKindPo();
                    po.setSkuId(dto.getSkuId());
                    po.setShopId(dto.getShopId());
                    po.setKindId(kindId);
                    return po;
                }).collect(Collectors.toList());
        skuKindMapper.batchInsert(skuKindList);
        return true;
    }
}
