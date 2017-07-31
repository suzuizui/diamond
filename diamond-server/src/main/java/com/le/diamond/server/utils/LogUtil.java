package com.le.diamond.server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogUtil {

    /**
     * Ĭ�ϵ���־
     */
    static public final Logger defaultLog = LoggerFactory.getLogger("com.le.diamond.server");

    /**
     * ����������Ҫ�澯
     */
    static public final Logger fatalLog = LoggerFactory
            .getLogger("com.le.diamond.server.fatal");

    /**
     * �ͻ���GET������ȡ���ݵ���־
     */
    static public final Logger pullLog = LoggerFactory
            .getLogger("com.le.diamond.server.pullLog");

    static public final Logger pullCheckLog = LoggerFactory
            .getLogger("com.le.diamond.server.pullCheckLog");
    /**
     * ��DB dump���ݵ���־
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
