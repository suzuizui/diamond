package com.opc.freshness.domain.bo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/18
 * sku制作统计 对象
 */
@Data
@NoArgsConstructor
public class SkuMakeBo {
    private Integer id;
    /**
     * sku编号
     */
    private Integer skuId;
    /**
     * sku名称
     */
    private String skuName;
    /**
     * 制作数量
     */
    private int makeCount;
    /**
     * 废弃数量
     */
    private int abortCount;
    /**
     * 报损数量
     */
    private int lossCount;

    public SkuMakeBo(Integer id, Integer skuId, String skuName, Integer makeCount) {
        this.id = id;
        this.skuId = skuId;
        this.skuName = skuName;
        this.makeCount = makeCount;
    }

    public String getAbortPercent() {
        if (makeCount ==0){
            return new BigDecimal(0).toString() + "%";

        }
        return new BigDecimal(abortCount).divide(new BigDecimal(makeCount), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).toString() + "%";
    }

    public String getLossPercent() {
        if (makeCount ==0){
            return new BigDecimal(0).toString() + "%";

        }
        return new BigDecimal(lossCount).divide(new BigDecimal(makeCount), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).toString() + "%";
    }
}
