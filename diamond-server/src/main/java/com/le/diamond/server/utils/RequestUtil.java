package com.le.diamond.server.utils;

import javax.servlet.http.HttpServletRequest;


public class RequestUtil {

    public static String getRemoteIp(HttpServletRequest request) {
        String nginxHeader = request.getHeader("X-Real-IP");
        return (nginxHeader == null) ? request.getRemoteAddr() : nginxHeader;
    }
}
