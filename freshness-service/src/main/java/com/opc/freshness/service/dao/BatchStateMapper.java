package com.opc.freshness.service.dao;

import com.opc.freshness.po.BatchStatePo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BatchStateMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(BatchStatePo record);

    BatchStatePo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BatchStatePo record);
}