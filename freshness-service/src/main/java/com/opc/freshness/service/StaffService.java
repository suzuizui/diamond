package com.opc.freshness.service;

import com.opc.freshness.domain.vo.StaffVo;

/**
 * AUTHOR: qishang
 * DATE:2017/7/17.
 */
public interface StaffService {
    /**
     * 通过卡号查找员工
     *
     * @param staffCode
     * @return
     */
    StaffVo selectByCardCode(String staffCode);
}
