package com.opc.freshness.controller;

import com.opc.freshness.common.Result;
import com.opc.freshness.common.Success;
import com.opc.freshness.domain.po.BatchPo;
import com.opc.freshness.service.biz.BatchBiz;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by qishang on 2017/7/12.
 */
@RestController
public class BatchController extends BaseController {
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(BatchController.class);
    /**
     * 批次服务
     */
    @Autowired
    private BatchBiz batchBiz;

    @RequestMapping(value = "/api/batch/insert/v1", method = {RequestMethod.POST, RequestMethod.GET})
    public Result insert(@RequestParam(required = false) Integer shopId) {

        BatchPo batchPo = new BatchPo();
        batchPo.setShopId(123123123);
        batchBiz.addBatch(batchPo);
        return new Success("成功");
    }
}
