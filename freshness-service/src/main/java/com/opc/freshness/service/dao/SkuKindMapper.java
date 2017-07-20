package com.opc.freshness.service.dao;

import com.opc.freshness.domain.bo.SkuPeakBo;
import com.opc.freshness.domain.po.SkuKindPo;
import com.opc.freshness.domain.vo.KindVo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface SkuKindMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(SkuKindPo record);

    int batchInsert(List<SkuKindPo> list);

    SkuKindPo selectByPrimaryKey(Integer id);

    List<KindVo> selectKind(@Param("skuId") Integer skuId, @Param("shopId") Integer shopId);

    List<SkuPeakBo> selectWithPeakInfo(@Param("shopId") Integer shopId, @Param("kindId") Integer kindId, @Param("peakId") Integer peakId,@Param("date") Date target);

    @MapKey("skuId")
    Map<Integer,SkuPeakBo> selectSkuMakeList(@Param("shopId") Integer shopId, @Param("kindId") Integer kindId, @Param("begin") Date beginDate,@Param("end") Date endDate);

    List<SkuKindPo> selectByRecord(SkuKindPo record);

    int updateByPrimaryKeySelective(SkuKindPo record);

}