package com.opc.freshness.service.dao;

import com.opc.freshness.domain.po.BatchPo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BatchMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BatchPo record);

    int insertSelective(BatchPo record);

    BatchPo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BatchPo record);

    int updateByPrimaryKey(BatchPo record);
}