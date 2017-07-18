package com.opc.freshness.service.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.opc.freshness.domain.po.SkuKindPo;
import com.opc.freshness.domain.vo.KindVo;

@Mapper
public interface SkuKindMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(SkuKindPo record);

    int batchInsert(List<SkuKindPo> list);

    SkuKindPo selectByPrimaryKey(Integer id);

    List<KindVo> selectKind(@Param("skuId") Integer skuId, @Param("shopId") Integer shopId);

    List<SkuKindPo> selectByRecord(SkuKindPo record);

    int updateByPrimaryKeySelective(SkuKindPo record);
}