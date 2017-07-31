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
 * ֪ͨ�����ڵ�ȡ�������ݵķ��� �������ݱ���¼���֪ͨ���е�server��
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

    // XXX ���˷���ϵͳbeta�����ı�notify.do�ӿڣ�����lastModifed����ͨ��Http header����
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

            conn.connect(); // ����TCP����

            int respCode = conn.getResponseCode(); // �����ڲ���������
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

    // ������server�����ӳ�ʱ��socket��ʱ
    static final int TIMEOUT = 5000;

    private final TaskManager notifyTaskManager;

}
