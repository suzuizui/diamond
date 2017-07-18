package com.opc.freshness.service.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.opc.freshness.domain.po.BatchPo;

@Mapper
public interface BatchMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(BatchPo record);

    BatchPo selectByPrimaryKey(Integer id);

    List<BatchPo> selectByRecord(BatchPo po);

    int updateByPrimaryKeySelective(BatchPo record);

}