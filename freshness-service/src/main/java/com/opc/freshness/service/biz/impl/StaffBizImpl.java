package com.opc.freshness.service.biz.impl;

import com.opc.freshness.domain.po.EmployeePo;
import com.opc.freshness.domain.vo.StaffVo;
import com.opc.freshness.service.biz.StaffBiz;
import com.opc.freshness.service.dao.EmployeeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by qishang on 2017/7/17.
 */
@Service
public class StaffBizImpl implements StaffBiz {
    @Resource
    private EmployeeMapper employeeMapper;

    @Override
    public StaffVo selectByStaffCode(String cardCode) {
        EmployeePo po = employeeMapper.selectByCardCode(cardCode);
        // TODO: 2017/7/17 查询员工姓名
        return StaffVo.builder().emplayeeId(po.getEmployeeId()).build();
    }
}
