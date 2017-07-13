package com.opc.freshness.service.dao;

import com.opc.freshness.domain.po.BatchStatePo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BatchStateMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(BatchStatePo record);

    int batchInsert(List<BatchStatePo> list);
    BatchStatePo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BatchStatePo record);
}