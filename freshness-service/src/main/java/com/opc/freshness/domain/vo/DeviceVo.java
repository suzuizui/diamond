package com.opc.freshness.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by qishang on 2017/7/17.
 */
@Data
@Builder
public class DeviceVo {
    /**
     * 门店信息
     */
    private ShopVo shopInfo;
    /**
     * 品类信息
     */
    private List<KindVo> categories;
    /**
     * 关联设备Id
     */
    private List<String> contactIds;
}
