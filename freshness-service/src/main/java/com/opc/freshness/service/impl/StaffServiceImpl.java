package com.opc.freshness.service.impl;

import com.opc.freshness.domain.vo.StaffVo;
import com.opc.freshness.service.StaffService;
import com.opc.freshness.service.integration.FeedBackHystrixService;
import com.opc.freshness.service.integration.domain.FeedBackUser;
import com.wormpex.biz.BizException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * AUTHOR: qishang
 * DATE:2017/7/17.
 */
@Service
public class StaffServiceImpl implements StaffService {
    //    @Resource
//    private StaffBiz staffBiz;
    @Resource
    private FeedBackHystrixService feedBackHystrixService;

    @Override
    public StaffVo selectByCardCode(String cardCode) {
//        EmployeePo employeePo = staffBiz.selectByCardCode(cardCode);
//        if (employeePo == null) {
//            throw new BizException("此员工未在鲜度系统注册");
//        }
        FeedBackUser user = feedBackHystrixService.queryMemberByUserno(cardCode);
        if (user == null) {
            throw new BizException("蜂利器中未查找到此员工");
        }
        return StaffVo.builder().emplayeeId(user.getJobNumber()).name(user.getName()).build();
    }
}
