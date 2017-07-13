package com.opc.freshness.service.dao;

import com.opc.freshness.po.BatchPo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BatchMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(BatchPo record);

    BatchPo selectByPrimaryKey(Integer id);

    List<BatchPo> selectByRecord(BatchPo po);

    int updateByPrimaryKeySelective(BatchPo record);

}