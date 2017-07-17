package com.opc.freshness.service.biz;

import com.opc.freshness.common.util.Pager;
import com.opc.freshness.domain.dto.BatchDto;
import com.opc.freshness.domain.po.BatchPo;
import com.opc.freshness.domain.po.BatchStatePo;
import com.opc.freshness.domain.vo.BatchLogVo;

import java.util.List;

/**
 * Created by qishang on 2017/7/12.
 */
public interface BatchBiz {
    /**
     * 添加一个批次
     *
     * @param batchDto
     * @return
     */
    public boolean addBatch(BatchDto batchDto);

    /**
     * 批次报损
     *
     * @param batchDto
     * @return
     */
    public boolean batchLoss(BatchDto batchDto);

    /**
     * 批次废弃
     *
     * @param batchDto
     * @return
     */
    public boolean batchAbort(BatchDto batchDto);

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
    Pager<BatchLogVo> selectLogByPage(Integer shopId, List statusList, Integer pageNo, Integer pageSize);
}
