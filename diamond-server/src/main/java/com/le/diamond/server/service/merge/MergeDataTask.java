package com.le.diamond.server.service.merge;

import com.le.diamond.notify.utils.task.Task;

/**
 * ��ʾ�����ݽ��оۺϵ�����
 * 
 * @author jiuRen
 */
class MergeDataTask extends Task {

    MergeDataTask(String dataId, String groupId) {
        this.dataId = dataId;
        this.groupId = groupId;
        
        // �ۺ��ӳ�
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