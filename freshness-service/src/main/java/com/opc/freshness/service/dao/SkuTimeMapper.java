package com.opc.freshness.service.dao;

import com.opc.freshness.domain.po.SkuTimePo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SkuTimeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SkuTimePo record);

    SkuTimePo selectByPrimaryKey(Integer id);

    SkuTimePo selectBySkuIdAndKindId(@Param("skuId") Integer skuId, @Param("kindId") Integer kindId);

    int updateByPrimaryKeySelective(SkuTimePo record);
}