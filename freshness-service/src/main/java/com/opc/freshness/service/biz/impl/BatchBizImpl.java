package com.opc.freshness.service.biz.impl;

import com.opc.freshness.domain.po.BatchPo;
import com.opc.freshness.domain.po.BatchStatePo;
import com.opc.freshness.service.biz.BatchBiz;
import com.opc.freshness.service.dao.BatchMapper;
import com.opc.freshness.service.dao.BatchStateMapper;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * Created by qishang on 2017/7/12.
 */
@Service
public class BatchBizImpl implements BatchBiz {
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(BatchBizImpl.class);

    @Resource
    private BatchMapper batchMapper;
    @Resource
    private BatchStateMapper batchStateMapper;

    public int addBatch(BatchPo batch) {
        return batchMapper.insert(batch);
    }

    public int addBatchLog(BatchStatePo batchState) {
        return batchStateMapper.insert(batchState);
    }
}
