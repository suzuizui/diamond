package com.opc.freshness.service.dao;

import com.opc.freshness.domain.po.BatchStatePo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BatchStateMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BatchStatePo record);

    int insertSelective(BatchStatePo record);

    BatchStatePo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BatchStatePo record);

    int updateByPrimaryKey(BatchStatePo record);
}