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
    @NotNull(message = "店铺Id不能为空")
    private String shopId;
    /**
     * sku列表
     */
    @NotEmpty(message = "sku列表不能为空")
    private List<SkuDto> skuList;
    /**
     * 操作人
     */
    @NotNull(message = "操作人不能为空")
    private String operator;
    /**
     * 单位
     */
    @NotNull(message = "单位不能为空")
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
