package com.opc.freshness.domain.po;

import lombok.Data;

import java.util.Date;

@Data
public class BatchPo {
    /**
     * id
     */
    private Integer id;
    /**
     * 种类Id
     */
    private Integer kindsId;
    /**
     * 门店Id
     */
    private Integer shopId;
    /**
     * 门店名称
     */
    private String shopName;
    /**
     * 状态
     *
     * @see status
     */
    private Integer status;
    /**
     * 预计废弃时间
     */
    private Date expiredTime;
    /**
     * 实际废弃时间
     */
    private Date expiredRealTime;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 批次总数量
     */
    private Integer totalCount;
    /**
     * 废弃数量
     */
    private Integer expiredCount;
    /**
     * 报损数量
     */
    private Integer breakCount;

    public static class status {
        /**
         * 准备中/回水中
         */
        public static final int ZBZ = 1;
        /**
         * 售卖中
         */
        public static final int SMZ = 2;
        /**
         * 待废弃
         */
        public static final int DFQ = 3;
        /**
         * 已售空
         */
        public static final int YSK = 4;
        /**
         * 已废弃
         */
        public static final int YFQ = 5;
    }

}