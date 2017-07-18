package com.opc.freshness.service.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.opc.freshness.common.util.PageRequest;
import com.opc.freshness.domain.po.BatchStatePo;
import com.opc.freshness.domain.vo.BatchLogVo;

@Mapper
public interface BatchStateMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(BatchStatePo record);

    int batchInsert(List<BatchStatePo> list);

    BatchStatePo selectByPrimaryKey(Integer id);

    int selectVoCount(@Param("shopId") Integer shopId, @Param("statusList") List<Integer> statusList);

    List<BatchLogVo> selectVoList(@Param("shopId") Integer shopId, @Param("statusList") List<Integer> statusList, @Param("page") PageRequest page);

    int updateByPrimaryKeySelective(BatchStatePo record);
}