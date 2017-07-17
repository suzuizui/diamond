package com.opc.freshness.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * Created by qishang on 2017/7/17.
 */
@Data
@Builder
public class StaffVo {
    /**
     * 姓名
     */
    private String name;
    /**
     * 员工编号
     */
    private String emplayeeId;
}
