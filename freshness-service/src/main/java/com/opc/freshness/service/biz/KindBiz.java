package com.opc.freshness.service.biz;

import com.opc.freshness.domain.bo.SkuPeakBo;
import com.opc.freshness.domain.po.KindPo;
import com.opc.freshness.domain.po.SkuKindPo;
import com.opc.freshness.domain.vo.KindVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/12
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
    List<KindVo> selectKindBySkuIdAndShopId(Integer skuId, Integer shopId);

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

    /**
     * 获得某个门店下某个大类下的sku列表
     *
     * @param shopId
     * @param kindId
     * @return
     */
    List<SkuKindPo> selectSkuList(Integer shopId, Integer kindId);

    /**
     * 高峰时，带销量预测的sku列表
     *
     * @param shopId
     * @param categoryId
     * @param PeakId
     * @param target     查询的日期
     * @return
     */
    List<SkuPeakBo> selectSkuListByPeak(Integer shopId, Integer categoryId, Integer PeakId, Date target);

    /**
     *获取sku制作情况
     * @param shopId
     * @param categoryId
     * @param beginDate
     * @param endDate
     * @return <skuId,SkuPeakBo>
     */
    Map<Integer,SkuPeakBo> selectSkuMakeList(Integer shopId, Integer categoryId, Date beginDate, Date endDate);
}
