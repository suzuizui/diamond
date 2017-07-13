package com.opc.freshness.service.integration;

import com.wormpex.cvs.product.api.bean.BeeProduct;

import java.util.Map;
import java.util.Set;

/**
 * Created by qishang on 2017/7/12.
 * 商品第三方服务
 */
public interface ProductService {
    /**
     * 通过SkuId和shopId查询商品
     *
     * @param shopId
     * @param skuIds
     * @return
     */
    Map<Integer, BeeProduct> queryProductMap(Integer shopId, Set<Integer> skuIds);
}
