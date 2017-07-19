package com.opc.freshness.service;

import com.opc.freshness.domain.bo.SkuDetailBo;
import com.opc.freshness.domain.bo.SkuKindBo;
import com.opc.freshness.domain.bo.SkuMakeBo;
import com.opc.freshness.domain.po.KindPo;
import com.opc.freshness.domain.vo.SkuVo;

import java.util.Date;
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
     *
     * @param barCode
     * @return
     */
    SkuVo selectSkuByBarCode(String barCode, Integer shopId);

    /**
     * 设置sku品类关联
     *
     * @param skuKindBo
     * @return
     */
    Boolean setkind(SkuKindBo skuKindBo);

    /**
     * 获得某个门店下某个大类下的sku列表
     *
     * @param shopId
     * @param categoryId
     * @return
     */
    List<SkuVo> selectSkuList(Integer shopId, Integer categoryId);

    /**
     * 获得某个品类下sku制作统计信息
     *
     * @param shopId
     * @param categoryId
     * @param date
     * @return
     */
    List<SkuMakeBo> skuMakeInfoList(Integer shopId, Integer categoryId, Date date);

    /**
     * 获得某个品类下sku流水信息
     * @param shopId
     * @param categoryId
     * @param date
     * @return
     */
    List<SkuDetailBo> skuDetailInfoList(Integer shopId, Integer categoryId, Date date);
}
