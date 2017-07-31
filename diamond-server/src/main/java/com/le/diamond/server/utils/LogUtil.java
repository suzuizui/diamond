package com.le.diamond.server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogUtil {

    /**
     * 默认的日志
     */
    static public final Logger defaultLog = LoggerFactory.getLogger("com.le.diamond.server");

    /**
     * 致命错误，需要告警
     */
    static public final Logger fatalLog = LoggerFactory
            .getLogger("com.le.diamond.server.fatal");

    /**
     * 客户端GET方法获取数据的日志
     */
    static public final Logger pullLog = LoggerFactory
            .getLogger("com.le.diamond.server.pullLog");

    static public final Logger pullCheckLog = LoggerFactory
            .getLogger("com.le.diamond.server.pullCheckLog");
    /**
     * 从DB dump数据的日志
     */
    static public final Logger dumpLog = LoggerFactory
            .getLogger("com.le.diamond.server.dumpLog");

    static public final Logger memoryLog = LoggerFactory
            .getLogger("com.le.diamond.server.monitorLog");

    static public final Logger clientLog = LoggerFactory
            .getLogger("com.le.diamond.server.clientLog");

    static public final Logger sdkLog = LoggerFactory
            .getLogger("com.le.diamond.server.sdkLog");

    static public final Logger traceLog = LoggerFactory
            .getLogger("com.le.diamond.server.traceLog");

    static public final Logger aclLog = LoggerFactory
            .getLogger("com.le.diamond.server.aclLog");
}
