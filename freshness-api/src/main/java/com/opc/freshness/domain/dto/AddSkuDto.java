package com.opc.freshness.domain.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by qishang on 2017/7/13.
 * 品类制作 数据传输对象
 */
@Data
public class AddSkuDto {
    /**
     * 店铺Id
     */
    @NotNull
    private String shopId;
    /**
     * sku列表
     */
    @NotEmpty
    private List<SkuDto> skuList;
    /**
     * 操作人
     */
    @NotNull
    private String operator;
    /**
     * 单位
     */
    private String unit;
    /**
     * 温度
     */
    private String degree;
    /**
     * 颜色
     */
    private String tag;
}
