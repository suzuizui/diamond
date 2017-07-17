package com.opc.freshness.service.biz.impl;

import com.opc.freshness.domain.po.EmployeePo;
import com.opc.freshness.service.biz.StaffBiz;
import com.opc.freshness.service.dao.EmployeeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * AUTHOR: qishang
 * DATE:2017/7/17.
 */
@Service
public class StaffBizImpl implements StaffBiz {
    @Resource
    private EmployeeMapper employeeMapper;

    @Override
    public EmployeePo selectByCardCode(String cardCode) {
        return employeeMapper.selectByCardCode(cardCode);
    }
}
