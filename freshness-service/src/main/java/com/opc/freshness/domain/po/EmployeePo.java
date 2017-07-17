package com.opc.freshness.domain.po;

import lombok.Data;

import java.util.Date;

/**
 * 员工
 */
@Data
public class EmployeePo {
    /**
     * Id
     */
    private Integer id;
    /**
     * 卡号
     */
    private String cardCode;
    /**
     * 员工编号
     */
    private String employeeId;
    /**
     * 创建时间
     */
    private Date createTime;

}