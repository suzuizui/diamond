package com.opc.freshness.service.integration;

import com.wormpex.cvs.product.api.bean.BeeProduct;
import com.wormpex.cvs.product.api.bean.BeeShopProduct;

import java.util.Map;
import java.util.Set;

/**
 * Created by qishang on 2017/7/12.
 * 商品第三方服务
 */
public interface ProductService {
    /**
     * 通过SkuId和shopId查询Sku
     * @param shopId
     * @param skuIds
     * @return
     */
    Map<Integer, BeeProduct> queryProductMap(Integer shopId, Set<Integer> skuIds);

    /**
     * 通过SkuId和shopId查询shopSku
     *
     * @param shopId
     * @param skuIds
     * @return
     */
    Map<Integer, BeeShopProduct> queryShopProductMap(Integer shopId, Set<Integer> skuIds);
}
