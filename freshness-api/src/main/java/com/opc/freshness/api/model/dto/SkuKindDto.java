package com.opc.freshness.api.model.dto;

import lombok.Data;

import java.util.List;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/17.
 */
@Data
public class SkuKindDto {
    /**
     * 门店Id
     */
    private Integer shopId;
    /**
     * skuId
     */
    private Integer skuId;
    /**
     * 品类Ids
     */
    private List<Integer> categoryIds;
}
