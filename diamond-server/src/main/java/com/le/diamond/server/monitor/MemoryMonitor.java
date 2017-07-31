package com.le.diamond.server.monitor;

import com.le.diamond.server.service.ConfigService;
import com.le.diamond.server.utils.LogUtil;
import com.le.diamond.server.controller.DatumController;
import com.le.diamond.server.service.ClientTrackService;
import com.le.diamond.server.service.TimerTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
public class MemoryMonitor {

    @Autowired
    public MemoryMonitor(DatumController datumController) {
        DatumInsertMonitorTask task = new DatumInsertMonitorTask(datumController);
        TimerTaskService.scheduleWithFixedDelay(task, DELAY_SECONDS, DELAY_SECONDS,
                TimeUnit.SECONDS);

        TimerTaskService.scheduleWithFixedDelay(new PrintMemoryTask(), DELAY_SECONDS,
                DELAY_SECONDS, TimeUnit.SECONDS);
        
        TimerTaskService.scheduleWithFixedDelay(new PrintGetConfigResponeTask(), DELAY_SECONDS,
                DELAY_SECONDS, TimeUnit.SECONDS);
    }
    
    // =====================
    static final long DELAY_SECONDS = 10;
}

class PrintGetConfigResponeTask implements Runnable{
	@Override
	public void run() {
		LogUtil.memoryLog.info(ResponseMonitor.getStringForPrint());
	}
}

class PrintMemoryTask implements Runnable {
    @Override
    public void run() {
        int groupCount = ConfigService.groupCount();
        int subClientCount = ClientTrackService.subscribeClientCount();
        long subCount = ClientTrackService.subscriberCount();
        LogUtil.memoryLog.info("groupCount={}, subscriberClientCount={}, subscriberCount={}",
                new Object[] { groupCount, subClientCount, subCount });
    }
}


class DatumInsertMonitorTask implements Runnable {
    final DatumController controller;
    
    DatumInsertMonitorTask(DatumController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        long insertCount = controller.insertSuccessCount.stat();
        long failCount = controller.insertFailureCount.stat();

        LogUtil.memoryLog.info("insert success count={}, insert failure count={}", new Object[] {
                insertCount, failCount });
    }
}
