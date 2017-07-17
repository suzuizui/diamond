package com.opc.freshness.service.biz;

import com.opc.freshness.domain.po.KindPo;
import com.opc.freshness.domain.po.SkuKindPo;
import com.opc.freshness.domain.vo.KindVo;

import java.util.List;

/**
 * Created by qishang on 2017/7/12.
 */
public interface KindBiz {
    /**
     * 通过主键查询品类
     *
     * @param id
     * @return
     */
    KindPo selectByPrimaryKey(Integer id);

    /**
     * 通过设备Id查询种类列表
     *
     * @param deviceId
     * @return
     */
    List<KindPo> selectListByDeviceId(String deviceId);

    /**
     * 通过skuId和shopId查询种类
     *
     * @param skuId
     * @param shopId
     * @return
     */
    List<KindVo> selectKindBySkuIdAndShopId(int skuId, Integer shopId);

    /**
     * 查询所有品类
     *
     * @return
     */
    List<KindPo> selectAll();

    /**
     * 批量插入sku kind关联
     *
     * @param skuKindList
     * @return
     */
    int batchInsertSkuKinds(List<SkuKindPo> skuKindList);
}
