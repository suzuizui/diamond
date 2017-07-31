package com.le.diamond.server.service.trace;

import com.le.diamond.md5.MD5;
import com.le.diamond.server.utils.LogUtil;
import com.le.diamond.server.utils.SystemConfig;
import org.springframework.stereotype.Service;

@Service
public class ConfigTraceService {
    public static final String  PERSISTENCE_EVENT_PUB = "pub";
    public static final String  PERSISTENCE_EVENT_REMOVE = "remove";
    public static final String  PERSISTENCE_EVENT_MERGE = "merge";

    public static final String  NOTIFY_EVENT_OK = "ok";
    public static final String  NOTIFY_EVENT_ERROR = "error";
    public static final String  NOTIFY_EVENT_EXCEPTION = "exception";

    public static final String  DUMP_EVENT_OK = "ok";
    public static final String  DUMP_EVENT_REMOVE_OK = "remove-ok";
    public static final String  DUMP_EVENT_ERROR = "error";

    public static final String  PULL_EVENT_OK = "ok";
    public static final String  PULL_EVENT_NOTFOUND = "not-found";
    public static final String  PULL_EVENT_CONFLICT = "conflict";
    public static final String  PULL_EVENT_ERROR = "error";

    public static void logPersistenceEvent(String dataId, String group, long ts, String handleIp, String type , String content) {
        //localIp | dataid | group | ts | handleIp | event | type | [delayed = -1] | ext(md5)
        String md5 = content == null ? null : MD5.getInstance().getMD5String(content);
        LogUtil.traceLog.info("{}|{}|{}|{}|{}|{}|{}|{}|{}", new Object[]{SystemConfig.LOCAL_IP, dataId, group, ts, handleIp, "persist", type, -1, md5});
    }

    public static void logNotifyEvent(String dataId, String group, long ts, String handleIp, String type,long delayed, String targetIp) {
        //localIp | dataid | group | ts | handleIp | event | type | [delayed] | ext(targetIp)
        LogUtil.traceLog.info("{}|{}|{}|{}|{}|{}|{}|{}|{}", new Object[]{SystemConfig.LOCAL_IP, dataId, group, ts, handleIp, "notify", type, delayed, targetIp});
    }

    public static void logDumpEvent(String dataId, String group, long ts, String handleIp, String type, long delayed, long length) {
        //localIp | dataid | group | ts | handleIp | event | type | [delayed] | length
        LogUtil.traceLog.info("{}|{}|{}|{}|{}|{}|{}|{}|{}", new Object[]{SystemConfig.LOCAL_IP, dataId, group, ts, handleIp, "dump", type, delayed, length});
    }

    public static void logDumpAllEvent(String dataId, String group, long ts, String handleIp, String type) {
        //localIp | dataid | group | ts | handleIp | event | type | [delayed = -1]
        LogUtil.traceLog.info("{}|{}|{}|{}|{}|{}|{}|{}", new Object[]{SystemConfig.LOCAL_IP, dataId, group, ts, handleIp, "dump-all", type, -1});
    }

    public static void logPullEvent(String dataId, String group, long ts, String type, long delayed, String clientIp) {
        //localIp | dataid | group | ts | event | type | [delayed] | ext(clientIp)
        LogUtil.traceLog.info("{}|{}|{}|{}|{}|{}|{}|{}", new Object[]{SystemConfig.LOCAL_IP, dataId, group, ts, "pull", type, delayed, clientIp});
    }
}
