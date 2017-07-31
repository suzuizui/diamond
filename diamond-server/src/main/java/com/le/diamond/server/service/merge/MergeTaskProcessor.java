package com.le.diamond.server.service.merge;

import com.le.diamond.common.Constants;
import com.le.diamond.domain.ConfigInfo;
import com.le.diamond.domain.ConfigInfoAggr;
import com.le.diamond.domain.Page;
import com.le.diamond.notify.utils.task.Task;
import com.le.diamond.notify.utils.task.TaskProcessor;
import com.le.diamond.server.service.PersistService;
import com.le.diamond.server.service.ConfigDataChangeEvent;
import com.le.diamond.server.service.trace.ConfigTraceService;
import com.le.diamond.server.utils.SystemConfig;
import com.le.diamond.server.utils.event.EventDispatcher;
import com.le.diamond.utils.ContentUtils;
import com.le.diamond.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;



class MergeTaskProcessor implements TaskProcessor {
    final int PAGE_SIZE = 10000;

    MergeTaskProcessor(PersistService persistService, MergeDatumService mergeService) {
        this.persistService = persistService;
        this.mergeService = mergeService;
    }

    @Override
    public boolean process(String taskType, Task task) {
        MergeDataTask mergeTask = (MergeDataTask) task;
        final String dataId = mergeTask.dataId;
        final String group = mergeTask.groupId;

        try {
            //List<ConfigInfoAggr> datumList = persistService.findConfigInfoAggr(dataId, group);

            List<ConfigInfoAggr> datumList = new ArrayList<ConfigInfoAggr>();
            int rowCount = persistService.aggrConfigInfoCount(dataId, group);
            int pageCount = (int) Math.ceil(rowCount * 1.0 / PAGE_SIZE);
            for (int pageNo = 1; pageNo <= pageCount; pageNo++) {
                Page<ConfigInfoAggr> page = persistService.findConfigInfoAggrByPage(dataId, group, pageNo, PAGE_SIZE);
                if (page != null) {
                    datumList.addAll(page.getPageItems());
                    log.info("[merge-query] {}, {}, size/total={}/{}", new Object[] {dataId, group,datumList.size(),rowCount});
                }
            }

            final Timestamp time = TimeUtils.getCurrentTime();
            // ¾ÛºÏ
            if (datumList.size() > 0) {
                ConfigInfo cf = merge(dataId, group, datumList);

                persistService.insertOrUpdate(null, null, cf, time);

                log.info("[merge-ok] {}, {}, size={}, length={}, md5={}, content={}", new Object[] {
                        dataId, group, datumList.size(), cf.getContent().length(), cf.getMd5(),
                        ContentUtils.truncateContent(cf.getContent()) });

                ConfigTraceService.logPersistenceEvent(dataId, group, time.getTime(), SystemConfig.LOCAL_IP, ConfigTraceService.PERSISTENCE_EVENT_MERGE, cf.getContent());
            }
            // É¾³ý
            else {
                persistService.removeConfigInfo(dataId, group);

                log.warn("[merge-delete] delete config info because no datum. dataId=" + dataId
                        + ", groupId=" + group);

                ConfigTraceService.logPersistenceEvent(dataId, group, time.getTime(),  SystemConfig.LOCAL_IP, ConfigTraceService.PERSISTENCE_EVENT_REMOVE, null);
            }

            EventDispatcher.fireEvent(new ConfigDataChangeEvent(dataId, group, time.getTime()));

        } catch (Exception e) {
            mergeService.addMergeTask(dataId, group);
            log.info("[merge-error] " + dataId + ", " + group + ", " + e.toString(), e);
        }

        return true;
    }
    
    
    ConfigInfo merge(String dataId, String group, List<ConfigInfoAggr> datumList) {
        StringBuilder sb = new StringBuilder();
        for (ConfigInfoAggr aggrInfo : datumList) {
            sb.append(aggrInfo.getContent());
            sb.append(Constants.DIAMOND_LINE_SEPARATOR);
        }
        String content = sb.substring(0, sb.lastIndexOf(Constants.DIAMOND_LINE_SEPARATOR));
        return new ConfigInfo(dataId, group, content);
    }

    // =====================

    private static final Logger log = LoggerFactory.getLogger(MergeTaskProcessor.class);
    
    private PersistService persistService;
    private MergeDatumService mergeService;
}