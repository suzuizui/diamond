package com.opc.freshness.service.impl;

import com.opc.freshness.common.util.BeanCopyUtils;
import com.opc.freshness.common.util.CollectionUtils;
import com.opc.freshness.domain.vo.KindVo;
import com.opc.freshness.domain.vo.SkuVo;
import com.opc.freshness.service.SkuService;
import com.opc.freshness.service.biz.KindBiz;
import com.opc.freshness.service.biz.SkuBiz;
import com.opc.freshness.service.integration.ProductService;
import com.wormpex.cvs.product.api.bean.BeeProductDetail;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/20
 */
@Service
public class SkuServiceImpl implements SkuService {
    @Resource
    private SkuBiz SkuBiz;
    @Resource
    private KindBiz kindBiz;
    @Resource
    private ProductService productService;

    @Override
    public Boolean salePredictAdd(Integer shopId, String shopName, Integer skuId, String skuName, Integer peakTime, Integer adviseCount, Date saleDay) {
        return SkuBiz.addSalePredict(shopId, shopName, skuId, skuName, peakTime, adviseCount, saleDay);
    }

    @Override
    public Boolean addSkuTime(Integer skuId, Integer kindId, Integer delay, Integer expired) {
        return SkuBiz.addSkuTime(skuId, kindId, delay, expired);
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
}
