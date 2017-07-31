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
 * Time: ����7:48
 */
public class ACLInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    SwitchService switchService;
    @Autowired
    ACLService aclService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // aliyun��������׼����֤����������������׼����֤��ͨ������enableAccessControl���ƣ�Ĭ�ϲ�����
        if (!switchService.getSwitchBoolean(SwitchService.SWITCH_KEY_ENABLE_ACCESSCONTROL, false)) {
            return true;
        }

        // 1. ��������ops���������������Լ�Ⱥ������
        if (aclService.isTrustRequester(request)) {
            return true;
        }

        // 2. ��ͨhttp����, client������Ҫ����׼�����
        String appName = request.getHeader(Constants.CLIENT_APPNAME_HEADER);
        String requestTS = request.getHeader(Constants.CLIENT_REQUEST_TS_HEADER);
        String token = request.getHeader(Constants.CLIENT_REQUEST_TOKEN_HEADER);

        // 3. token == MD5(timestamp + appKey)
        if (!aclService.checkIdentity(appName, requestTS, token)) { // �Ƿ�������
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getOutputStream().println("check requester identity failed. appName:" + appName + ", timestamp:" + requestTS + ", token:" + token);
            return false;
        } else { // ע��Ӧ��check�ɹ�pass
            return true;
        }

    }
}