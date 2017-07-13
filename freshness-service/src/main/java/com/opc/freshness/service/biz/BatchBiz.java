package com.opc.freshness.service.biz;

import com.opc.freshness.dto.AddSkuDto;
import com.opc.freshness.po.BatchPo;
import com.opc.freshness.po.BatchStatePo;

/**
 * Created by qishang on 2017/7/12.
 */
public interface BatchBiz {
    /**
     * 添加一个批次
     *
     * @param skuDto
     * @return
     */
    public boolean addBatch(AddSkuDto skuDto);

    /**
     * 添加一条批次流水记录
     *
     * @param batchState
     * @return
     */
    public int addBatchLog(BatchStatePo batchState);



}
