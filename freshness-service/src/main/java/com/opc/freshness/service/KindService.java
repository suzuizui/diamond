package com.opc.freshness.service;

import com.opc.freshness.domain.bo.SkuKindBo;
import com.opc.freshness.domain.po.KindPo;
import com.opc.freshness.domain.vo.SkuVo;

import java.util.List;

/**
 * AUTHOR: qishang
 * DATE:2017/7/17.
 */
public interface KindService {
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
     * 通过条形码查询Sku信息
     * @param barCode
     * @return
     */
    SkuVo selectSkuByBarCode(String barCode, Integer shopId);

    /**
     * 设置sku品类关联
     * @param skuKindBo
     * @return
     */
    Boolean setkind(SkuKindBo skuKindBo);
}
