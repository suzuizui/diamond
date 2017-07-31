package com.le.diamond.server.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 定时任务服务
 */
public class TimerTaskService {

    private static ScheduledExecutorService scheduledExecutorService = Executors
            .newScheduledThreadPool(10, new ThreadFactory() {
                AtomicInteger count = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    t.setName("com.le.diamond.server.Timer-" + count.getAndIncrement());
                    return t;
                }
            });

    static public void scheduleWithFixedDelay(Runnable command, long initialDelay, long delay,
            TimeUnit unit) {
        scheduledExecutorService.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

}
