package com.le.diamond.server.service.dump;

import com.le.diamond.notify.utils.task.TaskManager;
import com.le.diamond.server.service.DiskUtil;
import com.le.diamond.server.service.PersistService;
import com.le.diamond.server.service.TimerTaskService;
import com.le.diamond.server.utils.GroupKey2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
public class DumpService {
    
    @Autowired
    public DumpService(PersistService persistService) {
        DiskUtil.clearAll();
        
        this.persistService = persistService;
        
        DumpProcessor processor = new DumpProcessor(this);
        DumpAllProcessor dumpAllProcessor = new DumpAllProcessor(this);

        dumpTaskMgr = new TaskManager("com.le.diamond.server.DumpTaskManager");
        dumpTaskMgr.setDefaultTaskProcessor(processor);
        dumpTaskMgr.addProcessor(DumpAllTask.taskId, dumpAllProcessor);


        Runnable dumpAll = new Runnable() {
            @Override
            public void run() {
                dumpTaskMgr.addTask(DumpAllTask.taskId, new DumpAllTask());
            }
        };
        TimerTaskService.scheduleWithFixedDelay(dumpAll, dumpAllIntervalInHour,
                dumpAllIntervalInHour, TimeUnit.HOURS);

        // initial dump all
        dumpAllProcessor.process(DumpAllTask.taskId, new DumpAllTask());
    }

    
    public void dump(String dataId, String group, long lastModified, String handleIp) {
        String groupKey = GroupKey2.getKey(dataId, group);
        dumpTaskMgr.addTask(groupKey, new DumpTask(groupKey, lastModified, handleIp));
    }
    
    // =====================

    static final int dumpAllIntervalInHour = 6; // È«Á¿dump¼ä¸ô
    
    final PersistService persistService;
    
    final TaskManager dumpTaskMgr;

}
