package com.opc.freshness.service.dao;

import com.opc.freshness.common.util.PageRequest;
import com.opc.freshness.domain.bo.SkuCountBo;
import com.opc.freshness.domain.po.BatchStatePo;
import com.opc.freshness.domain.vo.BatchLogVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface BatchStateMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(BatchStatePo record);

    int batchInsert(List<BatchStatePo> list);

    BatchStatePo selectByPrimaryKey(Integer id);

    int selectVoCount(@Param("shopId") Integer shopId, @Param("statusList") List<Integer> statusList);

    List<BatchLogVo> selectVoList(@Param("shopId") Integer shopId, @Param("statusList") List<Integer> statusList, @Param("page") PageRequest page);

    int updateByPrimaryKeySelective(BatchStatePo record);

    List<SkuCountBo> selectSkuCountByStatus(@Param("shopId") Integer shopId, @Param("kindId") Integer kindId,@Param("date") Date date, @Param("status") int staus);
}