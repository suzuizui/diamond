package com.opc.freshness.service.integration.impl;

import com.opc.freshness.service.integration.ProductService;
import com.wormpex.cvs.product.api.bean.BeeProduct;
import com.wormpex.cvs.product.api.bean.BeeProductDetail;
import com.wormpex.cvs.product.api.bean.BeeShopProduct;
import com.wormpex.cvs.product.api.remote.ProductRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by qishang on 2017/7/12.
 */
@Service
public class ProductServiceImpl implements ProductService {
    private final static Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    @Resource
    private ProductRemote productRemote;

    @Override
    public Map<Integer, BeeProduct> queryProductMap(Integer shopId, Set<Integer> skuIds) {
        List<BeeProduct> list = productRemote.queryProductList(shopId, skuIds);
        return list.stream().collect(Collectors.toMap(BeeProduct::getId, beeProduct -> beeProduct, (key1, key2) -> key1, HashMap::new));
    }
    @Override
    public Map<Integer, BeeShopProduct> queryShopProductMap(Integer shopId, Set<Integer> skuIds) {
        List<BeeShopProduct> list = productRemote.queryShopProductList(shopId, skuIds);
        return list.stream().collect(Collectors.toMap(BeeShopProduct::getProductId, beeShopProduct -> beeShopProduct, (key1, key2) -> key1, HashMap::new));
    }

    @Override
    public BeeProductDetail queryProductDetailByBarcode(String barcode) {
        return productRemote.queryProductDetailByBarcode(barcode);
    }

}
