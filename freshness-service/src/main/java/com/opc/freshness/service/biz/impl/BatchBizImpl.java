package com.opc.freshness.service.biz.impl;

import com.opc.freshness.domain.po.BatchPo;
import com.opc.freshness.service.biz.BatchBiz;
import com.opc.freshness.service.dao.BatchMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by qishang on 2017/7/12.
 */
@Service
public class BatchBizImpl implements BatchBiz {
    @Resource
    private BatchMapper batchMapper;

    public void insert(BatchPo batch) {
        batchMapper.insert(batch);
    }
}
