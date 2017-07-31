package com.le.diamond.server.service.notify;

import com.le.diamond.notify.utils.task.Task;


public class NotifyTask extends Task {

    private String dataId;
    private String group;
    private long lastModified;
    private int failCount;


    public NotifyTask(String dataId, String group, long lastModified) {
        this.dataId = dataId;
        this.group = group;
        this.lastModified = lastModified;
        setTaskInterval(3000L);
    }


    public String getDataId() {
        return dataId;
    }


    public void setDataId(String dataId) {
        this.dataId = dataId;
    }


    public String getGroup() {
        return group;
    }


    public void setGroup(String group) {
        this.group = group;
    }


    public int getFailCount() {
        return failCount;
    }


    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public void merge(Task task) {
        // 进行merge, 但什么都不做, 相同的dataId和group的任务，后来的会代替之前的

    }

}
