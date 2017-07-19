package com.opc.freshness.service.integration.domain;

import lombok.Data;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/19
 */
@Data
public class FeedBackUser {
    /*
     * 主键
	 */
    private Long id;
    /*
     * 组织ID
     */
    private Long departmentId;
    /*
     * 用户姓名
     */
    private String name;
    /*
     * 用户工号
     */
    private String jobNumber;
    /*
     * 手机号
     */
    private String phone;
    /*
     * 是否为领导 0 否 1 是
     */
    private Integer positionId;
    /*
     * 是否在职
     */
    private Boolean onJob;

    /*
     * 用工形式 0 兼职 1 正式员工'
     */
    private Integer worktype;

    /*
     * 职称
     */
    private String title;

    /*
     * 门店编号
     */
    private String shopno;

    private FeedBackOrg idOrg;
}
