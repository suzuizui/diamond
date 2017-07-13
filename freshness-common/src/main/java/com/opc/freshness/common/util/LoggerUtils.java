package com.opc.freshness.common.util;

import org.slf4j.Logger;
import org.springframework.util.Assert;

/**
 * 规范logger debug、info
 *
 * @author guangbing.dong
 */
public final class LoggerUtils {
    private LoggerUtils() {
    }

    public static void debug(Logger logger, String message) {
        Assert.notNull(logger, "logger must not null");
        Assert.notNull(message, "message must not null");
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    public static void info(Logger logger, String message) {
        Assert.notNull(logger, "logger must not null");
        Assert.notNull(message, "message must not null");
        if (logger.isInfoEnabled()) {
            logger.info(message);
        }
    }

}
