package com.opc.freshness.service.biz;

import com.opc.freshness.common.util.Pager;
import com.opc.freshness.domain.po.BatchPo;
import com.opc.freshness.domain.po.BatchStatePo;
import com.opc.freshness.domain.vo.BatchLogVo;

import java.util.List;

/**
 * Created by qishang on 2017/7/12.
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
     * 待废弃列表
     *
     * @param shopId
     * @return
     */
    public List<BatchPo> selectAbortList(Integer shopId);

    /**
     * 查询列表
     *
     * @param batchPo
     * @return
     */
    public List<BatchPo> selectByRecord(BatchPo batchPo);

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

}
