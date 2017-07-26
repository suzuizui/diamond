package com.opc.freshness.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by qishang on 2017/7/13.
 * 待废弃批次Vo
 */
@Data
@Builder
public class BatchVo {
    /**
     * 批次Id
     */
    private Integer batchId;
    /**
     * 品类Id
     */
    private Integer categoryId;
    /**
     * 批次名称
     */
    private String batchName;
    /**
     * @see com.opc.freshness.domain.po.BatchPo.status
     */
    private Integer status;
    /**
     * 数量
     */
    private int quanity;
    /**
     * 颜色
     */
    private String tag;
    /**
     * 制作时间
     */
    private Date createTime;

    /**
     * 预计废弃时间
     */
    private Date expiredTime;
    /**
     * 废弃倒计时(分钟)
     */
    private Long expireCountDown;

    /**
     * 鲜度标识 0:旧 1:新
     */
    private Byte freshFlag;
    /**
     * skuList
     */
    private List<SkuVo> skuList;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    public Date getCreateTime() {
        return createTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    public Date getExpiredTime() {
        return expiredTime;
    }


}
