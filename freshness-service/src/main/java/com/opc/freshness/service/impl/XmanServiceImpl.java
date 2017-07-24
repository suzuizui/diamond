package com.opc.freshness.service.impl;

import com.opc.freshness.domain.bo.OrderMachineBo;
import com.opc.freshness.domain.Result;
import com.opc.freshness.service.integration.XmanService;
import com.opc.freshness.utils.HttpClientUtils;
import com.wormpex.api.json.JsonUtil;
import com.wormpex.biz.BizException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/24
 */
@Service
public class XmanServiceImpl implements XmanService {
    @Value(value = "${xman.path:https://xman.blibee.com/}")
    private String XmanPath;
    private static final String RELEVANT_PATH = "bach/baseinfo/shop/admin/pad/ordermachine/relevantInfo/v1";

    @Override
    public OrderMachineBo relevantInfo(String deviceId) {
        String url = XmanPath + RELEVANT_PATH+"?"+"deviceId="+deviceId;
        String str = HttpClientUtils.doGet(url);
        Result result = JsonUtil.of(str, Result.class);

        if (result.isRet()) {
            return JsonUtil.of(JsonUtil.of(result.getData()),OrderMachineBo.class);
        } else {
            throw new BizException(result.getMsg());
        }
    }
}
