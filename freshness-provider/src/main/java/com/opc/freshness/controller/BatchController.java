package com.opc.freshness.controller;

import com.opc.freshness.common.Error;
import com.opc.freshness.common.Result;
import com.opc.freshness.common.Success;
import com.opc.freshness.domain.po.BatchPo;
import com.opc.freshness.service.biz.BatchBiz;
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
    @Autowired
    private BatchBiz batchBiz;

    @RequestMapping(value = "/api/batch/insert/v1", method = {RequestMethod.POST, RequestMethod.GET})
    public Result insert(@RequestParam(required = false) Integer shopId) {
        try {
            BatchPo batchPo = new BatchPo();
            batchPo.setShopId(123123123);
            batchBiz.insert(batchPo);
        } catch (Exception e) {
            e.printStackTrace();
            return new Error(e.getMessage());
        }
        return new Success("成功");
    }
}
