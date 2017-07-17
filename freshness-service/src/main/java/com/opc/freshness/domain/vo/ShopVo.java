package com.opc.freshness.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * Created by qishang on 2017/7/12.
 * 店铺
 */
@Data
@Builder
public class ShopVo {
    /**
     * 店铺Id
     */
    private Integer shopId;
    /**
     * 店铺名称
     */
    private String shopName;
}
