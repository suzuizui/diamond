package com.le.diamond.server.service.notify;

import com.le.diamond.notify.utils.task.TaskManager;
import com.le.diamond.server.service.ConfigDataChangeEvent;
import com.le.diamond.server.service.ServerListService;
import com.le.diamond.server.utils.GroupKey2;
import com.le.diamond.server.utils.event.EventDispatcher.Event;
import com.le.diamond.server.utils.event.EventDispatcher.EventListener;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 通知其他节点取最新数据的服务。 监听数据变更事件，通知所有的server。
 * 
 * @author jiuRen
 */
@Service
public class NotifyService extends EventListener {

    @Autowired
    public NotifyService(ServerListService serverListService) {
        notifyTaskManager = new TaskManager("com.le.diamond.NotifyTaskManager");
        notifyTaskManager.setDefaultTaskProcessor(new NotifyTaskProcessor(serverListService));
    }

    @Override
    public List<Class<? extends Event>> interest() {
        List<Class<? extends Event>> types = new ArrayList<Class<? extends Event>>();
        types.add(ConfigDataChangeEvent.class);
        return types;
    }

    @Override
    public void onEvent(Event event) {
        ConfigDataChangeEvent evt = (ConfigDataChangeEvent) event;
        String dataId = evt.dataId;
        String group = evt.group;
        long lastModified = evt.lastModifiedTs;
        notifyTaskManager.addTask(GroupKey2.getKey(dataId, group), new NotifyTask(dataId, group, lastModified));
    }

    // XXX 為了方便系统beta，不改变notify.do接口，新增lastModifed参数通过Http header传递
    static public final String NOTIFY_HEADER_LAST_MODIFIED = "lastModified";
    static public final String NOTIFY_HEADER_OP_HANDLE_IP = "opHandleIp";

    static public HttpResult invokeURL(String url, List<String> headers, String encoding) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();

            conn.setConnectTimeout(TIMEOUT);
            conn.setReadTimeout(TIMEOUT);
            conn.setRequestMethod("GET");

            if (null != headers && !StringUtils.isEmpty(encoding)) {
                for (Iterator<String> iter = headers.iterator(); iter.hasNext();) {
                    conn.addRequestProperty(iter.next(), iter.next());
                }
            }
            conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + encoding);

            conn.connect(); // 建立TCP连接

            int respCode = conn.getResponseCode(); // 这里内部发送请求
            String resp = null;
            
            if (HttpServletResponse.SC_OK == respCode) {
                resp = IOUtils.toString(conn.getInputStream());
            } else {
                resp = IOUtils.toString(conn.getErrorStream());
            }
            return new HttpResult(respCode, resp);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    static public class HttpResult {
        final public int code;
        final public String content;
        
        public HttpResult(int code, String content) {
            this.code = code;
            this.content = content;
        }
    }
    
    // ==========================

    static final Logger log = LoggerFactory.getLogger(NotifyService.class);

    // 和其他server的连接超时和socket超时
    static final int TIMEOUT = 5000;

    private final TaskManager notifyTaskManager;

}
