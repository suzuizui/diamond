package com.opc.freshness.service.biz.impl;

import com.opc.freshness.dto.AddSkuDto;
import com.opc.freshness.po.BatchPo;
import com.opc.freshness.po.BatchStatePo;
import com.opc.freshness.service.biz.BatchBiz;
import com.opc.freshness.service.dao.BatchMapper;
import com.opc.freshness.service.dao.BatchStateMapper;
import com.wormpex.biz.BizTemplate;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    public boolean addBatch(AddSkuDto skuDto) {

        return true;
    }

    public int addBatchLog(BatchStatePo batchState) {
        return batchStateMapper.insertSelective(batchState);
    }
}
