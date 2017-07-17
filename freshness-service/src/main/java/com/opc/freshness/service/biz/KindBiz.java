package com.opc.freshness.service.biz;

import com.opc.freshness.domain.dto.SkuKindDto;
import com.opc.freshness.domain.po.KindPo;
import com.opc.freshness.domain.vo.SkuVo;

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
     * 通过条形码查询Sku信息
     * @param barCode
     * @return
     */
    SkuVo selectSkuByBarCode(String barCode,Integer shopId);

    /**
     * 设置sku品类关联
     * @param skuKindDto
     * @return
     */
    Boolean setkind(SkuKindDto skuKindDto);
}
