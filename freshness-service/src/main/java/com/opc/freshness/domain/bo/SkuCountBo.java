package com.opc.freshness.domain.bo;

import lombok.Data;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/18
 */
@Data
public class SkuCountBo {
    private Integer id;
    private Integer skuId;
    private String skuName;
    private Integer count;
}
