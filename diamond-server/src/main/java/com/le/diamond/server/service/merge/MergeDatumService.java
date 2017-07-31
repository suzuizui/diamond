package com.le.diamond.server.service.merge;

import com.le.diamond.server.service.PersistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.le.diamond.domain.ConfigInfoChanged;
import com.le.diamond.notify.utils.task.TaskManager;


/**
 * 数据聚合服务。
 * 
 * 启动时做全量聚合 + 修改数据触发的单条聚合
 * 
 * @author jiuRen
 */
@Service
public class MergeDatumService {

    @Autowired
    public MergeDatumService(PersistService persistService) {
        mergeTasks = new TaskManager("com.le.diamond.MergeDatum");
        mergeTasks.setDefaultTaskProcessor(new MergeTaskProcessor(persistService, this));

        log.info("server start, schedule merge for all dataId.");
        for (ConfigInfoChanged item : persistService.findAllAggrGroup()) {
            addMergeTask(item.getDataId(), item.getGroup());
        }
        log.info("server start, schedule merge end.");
    }

    /**
     * 数据变更后调用，添加聚合任务
     */
    public void addMergeTask(String dataId, String groupId) {
        MergeDataTask task = new MergeDataTask(dataId, groupId);
        mergeTasks.addTask(task.getId(), task);
    }

    // =====================

    private static final Logger log = LoggerFactory.getLogger(MergeDatumService.class);
    
    final TaskManager mergeTasks;
    
}
