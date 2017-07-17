package com.opc.freshness.service.impl;

import com.opc.freshness.common.util.BeanCopyUtils;
import com.opc.freshness.common.util.CollectionUtils;
import com.opc.freshness.domain.bo.SkuKindBo;
import com.opc.freshness.domain.po.KindPo;
import com.opc.freshness.domain.po.SkuKindPo;
import com.opc.freshness.domain.vo.KindVo;
import com.opc.freshness.domain.vo.SkuVo;
import com.opc.freshness.service.KindService;
import com.opc.freshness.service.biz.KindBiz;
import com.opc.freshness.service.integration.ProductService;
import com.wormpex.cvs.product.api.bean.BeeProductDetail;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AUTHOR: qishang
 * DATE:2017/7/17.
 */
@Service
public class KindServiceImpl implements KindService {
    @Resource
    private KindBiz kindBiz;
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
    public SkuVo selectSkuByBarCode(String barCode, Integer shopId) {
        Boolean hasCategory = true;
        BeeProductDetail product = productService.queryProductDetailByBarcode(barCode);
        List<KindVo> kindVoList = kindBiz.selectKindBySkuIdAndShopId(product.getProductBase().getProductId(), shopId);
        if (CollectionUtils.isEmpty(kindVoList)) {
            hasCategory = false;
            kindVoList = BeanCopyUtils.convertList(kindBiz.selectAll(), KindVo.class);
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
    public Boolean setkind(SkuKindBo bo) {
        List<SkuKindPo> skuKindList =
                bo.getCategoryIds().stream().map(kindId -> {
                    SkuKindPo po = new SkuKindPo();
                    po.setSkuId(bo.getSkuId());
                    po.setShopId(bo.getShopId());
                    po.setKindId(kindId);
                    return po;
                }).collect(Collectors.toList());
        kindBiz.batchInsertSkuKinds(skuKindList);
        return true;
    }
}
