package com.le.diamond.server.service.notify;

import com.le.diamond.common.Constants;
import com.le.diamond.notify.utils.task.Task;
import com.le.diamond.notify.utils.task.TaskProcessor;
import com.le.diamond.server.service.trace.ConfigTraceService;
import com.le.diamond.server.utils.SystemConfig;
import com.le.diamond.server.service.ServerListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;


/**
 * 通知服务。数据库变更后，通知所有server，包括自己，加载新数据。
 */
public class NotifyTaskProcessor implements TaskProcessor {
    
    public NotifyTaskProcessor(ServerListService serverListService) {
        this.serverListService = serverListService;
    }
    
    @Override
    public boolean process(String taskType, Task task) {
        NotifyTask notifyTask = (NotifyTask) task;
        String dataId = notifyTask.getDataId();
        String group = notifyTask.getGroup();
        long lastModified = notifyTask.getLastModified();
        
        boolean isok = true;
        
        for (String ip : serverListService.getServerList()) {
            isok = notifyToDump(dataId, group,lastModified, ip) && isok;
        }
        return isok;
    }
    
    /**
     * 通知其他server
     */
    boolean notifyToDump(String dataId, String group,long lastModified, String serverIp) {
        long delayed = System.currentTimeMillis() - lastModified;
        try {
            // XXX 為了方便系统beta，不改变notify.do接口，新增lastModifed参数通过Http header传递
            List<String> headers = Arrays.asList(
                    NotifyService.NOTIFY_HEADER_LAST_MODIFIED, String.valueOf(lastModified),
                    NotifyService.NOTIFY_HEADER_OP_HANDLE_IP, SystemConfig.LOCAL_IP);

            String urlString = MessageFormat.format(URL_PATTERN, serverIp, dataId, group);

            NotifyService.HttpResult result = NotifyService.invokeURL(urlString, headers, Constants.ENCODE);
            if (result.code == 200) {
                //log.info("[notify-ok] {}, {}, to {}", new Object[] { dataId, group, serverIp });
                ConfigTraceService.logNotifyEvent(dataId, group, lastModified,  SystemConfig.LOCAL_IP, ConfigTraceService.NOTIFY_EVENT_OK, delayed, serverIp);
                return true;
            } else {
                log.error("[notify-error] {}, {}, to {}, result {}", new Object[] { dataId, group,
                        serverIp, result.code });
                // ConfigTraceService.logNotifyEvent(dataId, group, lastModified, ConfigTraceService.NOTIFY_EVENT_ERROR, delayed, serverIp);
                return false;
            }
        } catch (Exception e) {
            log.error(
                    "[notify-exception] " + dataId + ", " + group + ", to " + serverIp + ", "
                            + e.toString(), e);
            // ConfigTraceService.logNotifyEvent(dataId, group, lastModified, ConfigTraceService.NOTIFY_EVENT_EXCEPTION, delayed, serverIp);
            return false;
        }
    }
    
    // ================

    static final Logger log = LoggerFactory.getLogger(NotifyTaskProcessor.class);
    
    static final String URL_PATTERN = "http://{0}:8080/diamond-server/notify.do?method=notifyConfigInfo&dataId={1}&group={2}";
    
    final ServerListService serverListService;
}
