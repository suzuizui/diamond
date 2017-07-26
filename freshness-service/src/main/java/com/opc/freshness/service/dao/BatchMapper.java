package com.opc.freshness.service.dao;

import com.opc.freshness.domain.po.BatchPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface BatchMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(BatchPo record);

    BatchPo selectByPrimaryKey(Integer id);

    List<BatchPo> selectByRecord(BatchPo po);

    int updateByPrimaryKeySelective(BatchPo record);

    int updateByGroupFlagSelective(BatchPo record);

    /**
     * 选取
     *
     * @param shopId 门店Id
     * @param status 状态
     * @param count  个数
     * @return
     */
    List<BatchPo> selectLastNGroupByKindAndFlag(@Param("shopId") Integer shopId, @Param("status") Integer status, @Param("count") Integer count);

    /**
     * @param skuId
     * @param kindId
     * @param limit
     * @param shopId
     * @return
     */
    List<BatchPo> batchListBySkuIdAndKindId(@Param("skuId") Integer skuId, @Param("kindId") Integer kindId, @Param("shopId") Integer shopId, @Param("limit") Integer limit);

    Date selectNextDelayTime(@Param("now") Date now, @Param("shopId") Integer shopId);

    Date selectNextExpiredTime(@Param("now") Date now, @Param("shopId") Integer shopId);

}