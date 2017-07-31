package com.le.diamond.server.service.merge;

import com.le.diamond.notify.utils.task.Task;

/**
 * 表示对数据进行聚合的任务。
 * 
 * @author jiuRen
 */
class MergeDataTask extends Task {

    MergeDataTask(String dataId, String groupId) {
        this.dataId = dataId;
        this.groupId = groupId;
        
        // 聚合延迟
        setTaskInterval(DELAY);
        setLastProcessTime(System.currentTimeMillis());
    }

    @Override
    public void merge(Task task) {
    }

    public String getId() {
        return toString();
    }

    @Override
    public String toString() {
        return "MergeTask[" + dataId + ", " + groupId + "]";
    }
    
    // ======================
    static final long DELAY = 3000L;
    
    final String dataId;
    final String groupId;
}