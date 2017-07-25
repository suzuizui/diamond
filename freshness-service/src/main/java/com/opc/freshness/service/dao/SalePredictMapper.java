package com.opc.freshness.service.dao;

import com.opc.freshness.domain.po.SalePredictPo;
import com.opc.freshness.domain.po.SkuTimePo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SalePredictMapper {
    int deleteByPrimaryKey(Integer id);

    int save(SalePredictPo record);

    SalePredictPo selectByPrimaryKey(Integer id);

    List<SalePredictPo> selectByRecord(SalePredictPo po);

    int updateByPrimaryKeySelective(SalePredictPo record);

}