package com.opc.freshness.service.dao;

import com.opc.freshness.domain.po.KindPo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KindMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(KindPo record);

    KindPo selectByPrimaryKey(Integer id);

    KindPo selectByCode(String code);

    List<KindPo> selectAll();

    int updateByPrimaryKeySelective(KindPo record);

}