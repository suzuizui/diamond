package com.opc.freshness.service.impl;

import com.opc.freshness.domain.po.EmployeePo;
import com.opc.freshness.domain.vo.StaffVo;
import com.opc.freshness.service.StaffService;
import com.opc.freshness.service.biz.StaffBiz;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * AUTHOR: qishang
 * DATE:2017/7/17.
 */
@Service
public class StaffServiceImpl implements StaffService {
    @Resource
    private StaffBiz staffBiz;

    @Override
    public StaffVo selectByCardCode(String cardCode) {
        EmployeePo employeePo = staffBiz.selectByCardCode(cardCode);
        // TODO: 2017/7/17 员工姓名
        return StaffVo.builder().emplayeeId(employeePo.getEmployeeId()).build();
    }
}
