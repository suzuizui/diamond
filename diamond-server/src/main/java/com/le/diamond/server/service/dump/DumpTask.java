package com.le.diamond.server.service.dump;

import com.le.diamond.domain.ConfigInfo;
import com.le.diamond.domain.Page;
import com.le.diamond.md5.MD5;
import com.le.diamond.notify.utils.task.Task;
import com.le.diamond.notify.utils.task.TaskProcessor;
import com.le.diamond.server.service.AggrWhitelist;
import com.le.diamond.server.service.ConfigService;
import com.le.diamond.server.service.PersistService;
import com.le.diamond.server.service.acl.ACLService;
import com.le.diamond.server.service.trace.ConfigTraceService;
import com.le.diamond.server.utils.GroupKey2;
import com.le.diamond.server.utils.LogUtil;


public class DumpTask extends Task {

    public DumpTask(String groupKey, long lastModified, String handleIp) {
        this.groupKey = groupKey;
        this.lastModified = lastModified;
        this.handleIp = handleIp;
        setTaskInterval(1000L); // retry interval: 1s
    }

    @Override
    public void merge(Task task) {
    }

    final String groupKey;
    final long lastModified;
    final String handleIp;
}

class DumpAllTask extends Task {
    @Override
    public void merge(Task task) {
    }

    static final String taskId = "dumpAllConfigTask";
}


class DumpProcessor implements TaskProcessor {

    DumpProcessor(DumpService dumpService) {
        this.dumpService = dumpService;
    }

    @Override
    public boolean process(String taskType, Task task) {
        DumpTask dumpTask = (DumpTask) task;
        String[] pair = GroupKey2.parseKey(dumpTask.groupKey);
        String dataId = pair[0];
        String group = pair[1];
        long lastModified = dumpTask.lastModified;
        String handleIp = dumpTask.handleIp;

        ConfigInfo cf = dumpService.persistService.findConfigInfo(dataId, group);

        if (dataId.equals(AggrWhitelist.AGGRIDS_METADATA)) {
            if (null != cf) {
                AggrWhitelist.load(cf.getContent());
            } else {
                AggrWhitelist.load(null);
            }
        }

        if (dataId.equals(ACLService.ACL_REGISTERAPP_DATAID)) {
            if (null != cf) {
                ACLService.loadRegisterApp(cf.getContent());
            } else {
                ACLService.loadRegisterApp(null);
            }
        }

        if (dataId.equals(ACLService.ACL_TRUSTIPS_DATAID)) {
            if (null != cf) {
                ACLService.loadTrustIps(cf.getContent());
            } else {
                ACLService.loadTrustIps(null);
            }
        }

        long delayed = System.currentTimeMillis() - lastModified;

        boolean result;
        if (null != cf) {
            result = ConfigService.dump(dataId, group, cf.getContent(), lastModified);

            if (result) {
                ConfigTraceService.logDumpEvent(dataId, group, lastModified, handleIp, ConfigTraceService.DUMP_EVENT_OK, delayed, cf.getContent().length());
            }
        } else {
            result = ConfigService.remove(dataId, group);

            if (result) {
                ConfigTraceService.logDumpEvent(dataId, group, lastModified, handleIp, ConfigTraceService.DUMP_EVENT_REMOVE_OK, delayed, 0);
            }
        }
        return result;
    }

    // =====================
    final DumpService dumpService;
}


class DumpAllProcessor implements TaskProcessor {

    DumpAllProcessor(DumpService dumpService) {
        this.dumpService = dumpService;
        this.persistService = dumpService.persistService;
    }

    @Override
    public boolean process(String taskType, Task task) {
        int rowCount = persistService.configInfoCount();
        int pageCount = (int) Math.ceil(rowCount * 1.0 / PAGE_SIZE);

        int actualRowCount = 0;
        for (int pageNo = 1; pageNo <= pageCount; pageNo++) {
            Page<PersistService.ConfigInfoWrapper> page = persistService.findAllConfigInfoForDumpAll(pageNo, PAGE_SIZE);
            if (page != null) {
                for (PersistService.ConfigInfoWrapper cf : page.getPageItems()) {

                    if (cf.getDataId().equals(AggrWhitelist.AGGRIDS_METADATA)) {
                        AggrWhitelist.load(cf.getContent());
                    }

                    if (cf.getDataId().equals(ACLService.ACL_REGISTERAPP_DATAID)) {
                        ACLService.loadRegisterApp(cf.getContent());
                    }

                    if (cf.getDataId().equals(ACLService.ACL_TRUSTIPS_DATAID)) {
                        ACLService.loadTrustIps(cf.getContent());
                    }

                    boolean result = ConfigService.dump(cf.getDataId(), cf.getGroup(), cf.getContent(), cf.getLastModified());
                    /*
                    ConfigTraceService.logDumpAllEvent(cf.getDataId(), cf.getGroup(), cf.getLastModified(),
                            result ? ConfigTraceService.DUMP_EVENT_OK : ConfigTraceService.DUMP_EVENT_ERROR);
                    */

                    final String content = cf.getContent();
                    final String md5 = MD5.getInstance().getMD5String(content);
                    LogUtil.dumpLog.info("[dump-all-ok] {}, {}, length={}, md5={}", new Object[]{
                            GroupKey2.getKey(cf.getDataId(), cf.getGroup()), cf.getLastModified(),
                            content.length(), md5});
                }

                actualRowCount += page.getPageItems().size();
                LogUtil.defaultLog.info("[all-dump] {} / {}", actualRowCount, rowCount);
            }
        }
        return true;
    }

    // =====================
    static final int PAGE_SIZE = 1000;

    final DumpService dumpService;
    final PersistService persistService;
}
