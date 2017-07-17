package com.opc.freshness.domain.dto;

import com.google.common.collect.Lists;
import com.wormpex.api.json.JsonUtil;
import lombok.Data;

import java.util.List;

/**
 * Created by qishang on 2017/7/17.
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
