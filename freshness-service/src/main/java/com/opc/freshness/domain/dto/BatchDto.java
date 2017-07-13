package com.opc.freshness.domain.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Date;
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
    @NotNull(message = "店铺Id不能为空")
    private Integer shopId;
    /**
     * 品类Id
     */
    @NotNull(message = "品类code不能为空")
    private String kindCode;
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
     * 创建时间
     */
    @NotNull(message = "制作时间不能为空")
    private Date createTime;
    /**
     * 操作类型 1:制作 2：报损 3：废弃
     */
    @NotNull(message = "操作类型不能为空")
    private Integer option;
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
