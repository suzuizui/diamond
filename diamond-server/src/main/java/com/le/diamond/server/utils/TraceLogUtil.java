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
    // ��¼server�����ӿڵ������¼
    public static Logger requestLog = LoggerFactory.getLogger("com.le.diamond.server.request");

    // ��¼����client����ѯ�����¼
    public static Logger pollingLog = LoggerFactory.getLogger("com.le.diamond.server.polling");


}
