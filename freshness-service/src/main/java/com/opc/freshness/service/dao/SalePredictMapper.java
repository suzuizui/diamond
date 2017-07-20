package com.opc.freshness.service.dao;

import com.opc.freshness.domain.po.SalePredictPo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SalePredictMapper {
    int deleteByPrimaryKey(Integer id);

    int save(SalePredictPo record);

    SalePredictPo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SalePredictPo record);

}