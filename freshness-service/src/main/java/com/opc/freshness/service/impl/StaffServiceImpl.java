package com.opc.freshness.service.impl;

import com.opc.freshness.domain.po.EmployeePo;
import com.opc.freshness.domain.vo.StaffVo;
import com.opc.freshness.service.StaffService;
import com.opc.freshness.service.biz.StaffBiz;
import com.wormpex.biz.BizException;
import com.wormpex.biz.lang.Biz;
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
        if (employeePo == null) {
            throw new BizException("此员工未在鲜度系统注册");
        }
        // TODO: 2017/7/17 员工姓名
        return StaffVo.builder().emplayeeId(employeePo.getEmployeeId()).build();
    }
}
