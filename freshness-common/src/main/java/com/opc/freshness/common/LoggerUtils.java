package com.opc.freshness.common;

import org.slf4j.Logger;
import org.springframework.util.Assert;

/**
 * 规范logger debug、info
 * 
 * @author guangbing.dong
 *
 */
public final class LoggerUtils {
    private LoggerUtils() {
    }

    public static void debug(Logger logger, String message) {
        Assert.notNull(logger);
        Assert.notNull(message);
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    public static void info(Logger logger, String message) {
        Assert.notNull(logger);
        Assert.notNull(message);
        if (logger.isInfoEnabled()) {
            logger.info(message);
        }
    }

}
