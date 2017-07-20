package com.opc.freshness.domain.bo;

import com.opc.freshness.domain.vo.KindVo;
import lombok.Data;

import java.util.List;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/20
 */
@Data
public class SkuPeakBo {
    /**
     * 商品SkuId
     */
    private Integer skuId;
    /**
     * 商品Sku名称
     */
    private String skuName;
    /**
     * 图片地址
     */
    private String imgUrl;
    /**
     * 制作个数
     */
    private Integer count;
    /**
     * 废弃数量
     */
    private Integer abortCount;
    /**
     * 品类信息
     */
    private List<KindVo> categories;
    /**
     * 是否设置过品类
     */
    private Boolean hasCategory;
    /**
     * 销量预测个数
     */
    private Integer adviceCount;
}
