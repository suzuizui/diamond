package com.opc.freshness.service.biz;

import com.opc.freshness.domain.vo.StaffVo;

/**
 * Created by qishang on 2017/7/17.
 */
public interface StaffBiz {
    public StaffVo selectByStaffCode(String staffCode);
}
