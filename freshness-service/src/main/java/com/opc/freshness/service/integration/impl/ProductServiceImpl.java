package com.opc.freshness.service.integration.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.opc.freshness.service.integration.ProductService;
import com.wormpex.cvs.product.api.bean.BeeProduct;
import com.wormpex.cvs.product.api.bean.BeeProductDetail;
import com.wormpex.cvs.product.api.bean.BeeShopProduct;
import com.wormpex.cvs.product.api.remote.ProductRemote;

/**
 * AUTHOR: qishang DATE: 2017/7/12.
 */
@Service
public class ProductServiceImpl implements ProductService {
    @Resource
    private ProductRemote productRemote;

    @Override
    public Map<Integer, BeeProduct> queryProductMap(Integer shopId, Set<Integer> skuIds) {
        List<BeeProduct> list = productRemote.queryProductList(shopId, skuIds);
        return list.stream().collect(
                Collectors.toMap(BeeProduct::getId, beeProduct -> beeProduct, (key1, key2) -> key1, HashMap::new));
    }

    @Override
    public BeeProductDetail queryProductDetail(int productId) {
        return productRemote.queryProductDetail(productId);
    }

    @Override
    public Map<Integer, BeeShopProduct> queryShopProductMap(Integer shopId, Set<Integer> skuIds) {
        List<BeeShopProduct> list = productRemote.queryShopProductList(shopId, skuIds);
        return list.stream().collect(Collectors.toMap(BeeShopProduct::getProductId, beeShopProduct -> beeShopProduct,
                (key1, key2) -> key1, HashMap::new));
    }

    @Override
    public BeeProductDetail queryProductDetailByBarcode(String barcode) {
        return productRemote.queryProductDetailByBarcode(barcode);
    }

}
