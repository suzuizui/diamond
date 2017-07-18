package com.opc.freshness.config;

import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.ServletRequestHandledEvent;

import com.wormpex.monitor.WMonitor;
import com.wormpex.monitor.WMonitorType;

/**
 * HttpMonitorListener
 * 
 * @see org.springframework.context.support.AbstractApplicationContext#refresh()
 * 
 * @author guangbing.dong
 *
 */
@Component
public class HttpMonitorListener implements ApplicationListener<ServletRequestHandledEvent> {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(HttpMonitorListener.class);

    /**
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(ServletRequestHandledEvent event) {
        // has exception
        if (event.wasFailure()) {
            WMonitor.recordError("invoke [" + event.getRequestUrl() + "] error :", event.getFailureCause());
            logger.error(event.toString());
        } else {
            WMonitor.recordTransaction(WMonitorType.BIZ, "biz", event.getProcessingTimeMillis());
            if (logger.isInfoEnabled()) {
                logger.info(event.toString());
            }
        }

    }
}
