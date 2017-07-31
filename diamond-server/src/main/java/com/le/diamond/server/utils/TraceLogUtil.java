package com.le.diamond.server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: dingjoey
 * Date: 13-12-12
 * Time: 20:08
 */
public class TraceLogUtil {
    // 记录server各个接口的请求记录
    public static Logger requestLog = LoggerFactory.getLogger("com.le.diamond.server.request");

    // 记录各个client的轮询请求记录
    public static Logger pollingLog = LoggerFactory.getLogger("com.le.diamond.server.polling");


}
