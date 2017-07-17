package com.opc.freshness.utils;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/17.
 */
public class RedisKeyUtils {
    private static final String PREFIX = "freshness";
    private static final String LOCK = "lock";
    private static final String SPLIT = ":";

    public static String getLockKey(String jobName) {
        return append(append(PREFIX, LOCK), jobName);
    }

    private static String append(String src, String str) {
        return src + SPLIT + str;
    }
}
