package com.opc.freshness.service.integration;

import com.wormpex.cvs.product.api.bean.BeeProduct;
import com.wormpex.cvs.product.api.bean.BeeProductDetail;
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
     *
     * @param shopId
     * @param skuIds
     * @return
     */
    Map<Integer, BeeProduct> queryProductMap(Integer shopId, Set<Integer> skuIds);
    /**
     * 查询商品详情
     *
     * @param productId productId
     * @return 商品详情
     */
    BeeProductDetail queryProductDetail(int productId);

    /**
     * 通过SkuId和shopId查询shopSku
     *
     * @param shopId
     * @param skuIds
     * @return
     */
    Map<Integer, BeeShopProduct> queryShopProductMap(Integer shopId, Set<Integer> skuIds);

    /**
     * 根据条形码获取商品信息
     *
     * @param barcode 条码信息
     * @return
     */
    BeeProductDetail queryProductDetailByBarcode(String barcode);

    /**
     * 通过code查询商品
     * @param productCode
     * @return
     */
    BeeProduct queryByCode(String productCode);
}
