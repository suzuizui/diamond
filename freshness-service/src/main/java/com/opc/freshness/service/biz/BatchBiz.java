package com.opc.freshness.service.biz;

import com.opc.freshness.domain.dto.BatchDto;
import com.opc.freshness.domain.po.BatchStatePo;

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

}
