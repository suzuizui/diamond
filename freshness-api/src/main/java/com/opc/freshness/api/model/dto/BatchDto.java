package com.opc.freshness.api.model.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by qishang on 2017/7/13.
 * 品类制作 数据传输对象
 */
@Data
public class BatchDto {
    /**
     * 店铺Id
     */
    private Integer shopId;
    /**
     * 批次Id
     */
    private Integer batchId;
    /**
     * 操作类型 1:制作 2：报损 3：废弃
     */
    private Integer option;
    /**
     * 品类Id
     */
    private Integer categoryId;
    /**
     * sku列表
     */
    private List<SkuDto> skuList;
    /**
     * 操作人
     */
    private String operator;
    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 单位
     */
    private String unit;
    /**
     * 温度
     */
    private Integer degree;
    /**
     * 颜色
     */
    private String tag;
}
