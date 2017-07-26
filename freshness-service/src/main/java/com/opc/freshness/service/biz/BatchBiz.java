package com.opc.freshness.service.biz;

import com.opc.freshness.common.util.Pager;
import com.opc.freshness.domain.bo.SkuCountBo;
import com.opc.freshness.domain.bo.SkuDetailBo;
import com.opc.freshness.domain.po.BatchPo;
import com.opc.freshness.domain.po.BatchStatePo;
import com.opc.freshness.domain.vo.BatchLogVo;

import java.util.Date;
import java.util.List;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/12
 */
public interface BatchBiz {
    /**
     * 插入一条批次记录
     *
     * @param batch
     * @return
     */
    int insertSelective(BatchPo batch);

    /**
     * 批量插入批次流水
     *
     * @param logs
     * @return
     */
    int batchInsertLog(List<BatchStatePo> logs);

    /**
     * 查询一条批次记录
     *
     * @param batchId
     * @return
     */
    BatchPo selectByPrimaryKey(Integer batchId);

    /**
     * 制作中待废弃列表
     *
     * @param shopId
     * @return
     */
    List<BatchPo> selectMakeAndAbortList(Integer shopId);

    /**
     * 查询列表
     *
     * @param batchPo
     * @return
     */
    List<BatchPo> selectByRecord(BatchPo batchPo);
    /**
     * batch 不具有乐观锁的更新
     *
     * @param batchPo
     */
    int updateByPrimaryKeySelective(BatchPo batchPo);

    /**
     * 通过分组标识更新
     * @param record
     * @return
     */
    int updateByGroupFlagSelective(BatchPo record);
    /**
     * batch 具有乐观锁的更新
     *
     * @param batchPo
     */
    void updateBatchByPrimaryKeyLock(BatchPo batchPo);

    /**
     * @param shopId     门店Id
     * @param statusList 状态列表
     * @param pageNo     页码
     * @param pageSize   页面大小
     * @return
     */
    Pager<BatchLogVo> selectLogByPage(Integer shopId, List<Integer> statusList, Integer pageNo, Integer pageSize);

    /**
     * 查询sku数量
     *
     * @param shopId
     * @param kindId
     * @param date
     * @param staus
     * @return
     */
    List<SkuCountBo> selectSkuCountByStatus(Integer shopId, Integer kindId, Date date, int staus);

    /**
     * 查询某大类下的流水明细
     * @param shopId
     * @param categoryId
     * @param date
     * @return
     */
    List<SkuDetailBo> skuDetailInfoList(Integer shopId, Integer categoryId, Date date);

    /**
     * 查询批次明细
     * @param batchId
     * @return
     */
    List<SkuDetailBo> skuDetailInfoListByBatchId(Integer batchId);

    /**
     * 通过sku和大类获得批次列表
     * @param skuId
     * @param categoryId
     * @param shopId
     * @param limit
     * @return
     */
    List<BatchPo> batchListBySkuIdAndKindId(Integer skuId, Integer categoryId,Integer shopId, Integer limit);

    Date selectNextTime(Date now,Integer shopId);

    int saleOutOldBatch(Integer shopId, Integer kindId,Integer groupFlag);
}
