package com.opc.freshness.service.dao;

import com.opc.freshness.domain.po.EmployeePo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(EmployeePo record);

    EmployeePo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(EmployeePo record);

    /**
     * 通过卡号查询员工信息
     *
     * @param cardCode
     * @return
     */
    EmployeePo selectByCardCode(String cardCode);
}