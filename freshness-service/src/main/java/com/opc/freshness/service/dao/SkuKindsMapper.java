package com.opc.freshness.service.dao;

import com.opc.freshness.po.SkuKindsPo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SkuKindsMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(SkuKindsPo record);

    SkuKindsPo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SkuKindsPo record);

}