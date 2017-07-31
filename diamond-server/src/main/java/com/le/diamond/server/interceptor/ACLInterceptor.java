package com.le.diamond.server.interceptor;

import com.le.diamond.common.Constants;
import com.le.diamond.server.service.SwitchService;
import com.le.diamond.server.service.acl.ACLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * le.com Inc. Copyright (c) 1998-2101 All Rights Reserved.
 * <p/>
 * Project: diamond-server
 * User: qiaoyi.dingqy
 * Date: 13-11-13
 * Time: 下午7:48
 */
public class ACLInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    SwitchService switchService;
    @Autowired
    ACLService aclService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // aliyun环境开启准入验证，其他环境不开启准入验证；通过开关enableAccessControl控制，默认不开启
        if (!switchService.getSwitchBoolean(SwitchService.SWITCH_KEY_ENABLE_ACCESSCONTROL, false)) {
            return true;
        }

        // 1. 信任来自ops所有请求，信任来自集群间请求
        if (aclService.isTrustRequester(request)) {
            return true;
        }

        // 2. 普通http请求, client请求需要进行准入控制
        String appName = request.getHeader(Constants.CLIENT_APPNAME_HEADER);
        String requestTS = request.getHeader(Constants.CLIENT_REQUEST_TS_HEADER);
        String token = request.getHeader(Constants.CLIENT_REQUEST_TOKEN_HEADER);

        // 3. token == MD5(timestamp + appKey)
        if (!aclService.checkIdentity(appName, requestTS, token)) { // 非法请求者
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getOutputStream().println("check requester identity failed. appName:" + appName + ", timestamp:" + requestTS + ", token:" + token);
            return false;
        } else { // 注册应用check成功pass
            return true;
        }

    }
}