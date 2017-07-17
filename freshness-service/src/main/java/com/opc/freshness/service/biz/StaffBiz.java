package com.opc.freshness.service.biz;

import com.opc.freshness.domain.po.EmployeePo;

/**
 * AUTHOR: qishang
 * DATE:2017/7/17.
 */
public interface StaffBiz {
     EmployeePo selectByCardCode(String staffCode);
}
