package com.le.diamond.server.service;

import com.le.diamond.server.service.notify.NotifyService;
import com.le.diamond.server.utils.LogUtil;
import com.le.diamond.server.utils.SystemConfig;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class ServerListService {

    public ServerListService() {
        GetServerListTask task = new GetServerListTask();
        task.run();
        if (null == serverList || serverList.isEmpty()) {
            LogUtil.fatalLog.error("########## cannot get serverlist, so exit.");
            System.exit(0);
        } else {
            TimerTaskService.scheduleWithFixedDelay(task, 0L, 5L, TimeUnit.SECONDS);
        }
    }
    
    public List<String> getServerList() {
        return new ArrayList<String>(serverList);
    }
    
    public List<String> getServerListWithoutMe() {
        return new ArrayList<String>(serverListWithoutMe);
    }
    
    private void updateIfChanged(List<String> newList) {
        if (newList.isEmpty()) {
            return;
        }

        if (!newList.contains(SystemConfig.LOCAL_IP)) {
            newList.add(SystemConfig.LOCAL_IP);
            LogUtil.fatalLog.error("########## [serverlist] self ip {} not in serverlist ",
                    SystemConfig.LOCAL_IP, newList);
        }

        if (newList.equals(serverList)) {
            return;
        }

        serverList = new ArrayList<String>(newList);
        LogUtil.defaultLog.warn("[serverlist] updated to {}", serverList);

        serverListWithoutMe = new ArrayList<String>(newList);
        serverListWithoutMe.remove(SystemConfig.LOCAL_IP);
    }

    // 保证不返回NULL
    private List<String> getApacheServerList() {
        try {
            String url = "http://jmenv.tbsite.net:8080/diamond-server/diamond";
            NotifyService.HttpResult result = NotifyService.invokeURL(url, null, null);

            if (200 == result.code) {
                return IOUtils.readLines(new StringReader(result.content));
            } else {
                LogUtil.defaultLog.error("[serverlist] failed to get serverlist, error code {}", result.code);
                return Collections.emptyList();
            }
        } catch (IOException e) {
            LogUtil.defaultLog.error("[serverlist] exception, " + e.toString(), e);
            return Collections.emptyList();
        }
    }
    
    class GetServerListTask implements Runnable {
        @Override
        public void run() {
            try {
                updateIfChanged(getApacheServerList());
            } catch (Exception e) {
                LogUtil.defaultLog.error("[serverlist] failed to get serverlist, " + e.toString(), e);
            }
        }
    }
    
    // ==========================

    // 和其他server的连接超时和socket超时
    static final int TIMEOUT = 5000;
    
    private volatile List<String> serverList = new ArrayList<String>();
    private volatile List<String> serverListWithoutMe = new ArrayList<String>();
}
