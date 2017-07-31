package com.le.diamond.server.service;

import com.le.diamond.server.utils.GroupKey2;
import com.le.diamond.server.utils.LogUtil;
import com.le.diamond.server.controller.ConfigController;
import com.le.diamond.server.controller.ConfigServlet;
import com.le.diamond.server.utils.RequestUtil;
import com.le.diamond.server.utils.event.EventDispatcher.Event;
import com.le.diamond.server.utils.event.EventDispatcher.EventListener;
import org.springframework.stereotype.Service;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * 长轮询服务。负责处理：<ul>
 *  <li>记录长轮询请求
 *  <li>数据变更
 *  <li>超时处理
 * </ul>
 */
@Service
public class LongPullingService extends EventListener {

    public boolean isClientLongPolling(String clientIp) {
        return getClientPollingRecord(clientIp) != null;
    }

    public List<Map<String, String>> getAllClientSubConfig(String dataId, String group) {
        if(allSubs == null) return null;

        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String groupKey = GroupKey2.getKey(dataId, group);
        for (Iterator<ClientLongPulling> iter = allSubs.iterator(); iter.hasNext();) {
            ClientLongPulling clientSub = iter.next();
            if (clientSub.clientMd5Map.containsKey(groupKey)) {
                HttpServletRequest request = (HttpServletRequest) clientSub.asyncContext.getRequest();
                String ipAddr = RequestUtil.getRemoteIp(request);
                String connTime = format.format(new Date(clientSub.createTime));
                Map<String, String> map = new HashMap<String, String>();
                map.put("dataId", dataId);
                map.put("group", group);
                map.put("ipAddr", ipAddr);
                map.put("connTime", connTime);
                result.add(map);
            }
        }
        return result;
    }

    public Map<String, String> getClientSubConfigInfo(String clientIp) {
        ClientLongPulling record = getClientPollingRecord(clientIp);

        if(record == null) return Collections.<String, String>emptyMap();

        return record.clientMd5Map;
    }

    private ClientLongPulling getClientPollingRecord (String clientIp){
        if(allSubs == null) return null;

        for(ClientLongPulling clientLongPulling : allSubs) {
            HttpServletRequest request = (HttpServletRequest) clientLongPulling.asyncContext.getRequest();

            if(clientIp.equals(RequestUtil.getRemoteIp(request))){
                return clientLongPulling;
            }
        }

        return null;
    }

    public void addLongPullingClient(HttpServletRequest req, HttpServletResponse rsp,
            Map<String, String> clientMd5Map, int probeRequestSize) {
        
        // 一定要由HTTP线程调用，否则离开后容器会立即发送响应
        final AsyncContext asyncContext = req.startAsync();
        // AsyncContext.setTimeout()的超时时间不准，所以只能自己控制
        asyncContext.setTimeout(0L);

        scheduler.execute(new ClientLongPulling(asyncContext, clientMd5Map, probeRequestSize));
    }

    @Override
    public List<Class<? extends Event>> interest() {
        List<Class<? extends Event>> eventTypes = new ArrayList<Class<? extends Event>>();
        eventTypes.add(LocalDataChangeEvent.class);
        return eventTypes;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof LocalDataChangeEvent) {
            LocalDataChangeEvent evt = (LocalDataChangeEvent) event;
            scheduler.execute(new DataChangeTask(evt.groupKey));
        }
    }

    static public boolean isSupportLongPulling(HttpServletRequest req) {
        return null != req.getHeader(LONG_PULLING_HEADER);
    }
    
    static long getLongPullingTimeout(HttpServletRequest req) {
        String str = req.getHeader(LongPullingService.LONG_PULLING_HEADER);
        return Math.max(10000, Long.parseLong(str) - 500); // 提前500ms返回响应，为避免客户端超时 @qiaoyi.dingqy 2013.10.22改动
    }

    public LongPullingService() {
        allSubs = new LinkedList<ClientLongPulling>();

        scheduler = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                t.setName("com.le.diamond.LongPulling");
                return t;
            }
        });
        scheduler.scheduleWithFixedDelay(new StatTask(), 0L, 10L, TimeUnit.SECONDS);
    }

    // =================
    
    static public final String LONG_PULLING_HEADER = "longPullingTimeout";

    final ScheduledExecutorService scheduler;

    /**
     * 长轮询订阅关系
     */
    final List<ClientLongPulling> allSubs;

    // =================

    class DataChangeTask implements Runnable {
        @Override
        public void run() {
            for (Iterator<ClientLongPulling> iter = allSubs.iterator(); iter.hasNext();) {
                ClientLongPulling clientSub = iter.next();
                if (clientSub.clientMd5Map.containsKey(groupKey)) {
                    clientSub.sendResponse(Arrays.asList(groupKey), changeTime);
                    iter.remove(); // 删除订阅关系
                    LogUtil.clientLog.info("{}|{}|{}|{}|{}|{}|{}",
                            new Object[]{ (System.currentTimeMillis() - clientSub.createTime),
                                    "in-advance", RequestUtil.getRemoteIp((HttpServletRequest)  clientSub.asyncContext.getRequest()), "polling",
                                    clientSub.clientMd5Map.size(),  clientSub.probeRequestSize, groupKey});
                }
            }
        }

        DataChangeTask(String groupKey) {
            this.groupKey = groupKey;
        }

        final String groupKey;
        final long changeTime = System.currentTimeMillis();
    }

    // =================

    class StatTask implements Runnable {
        @Override
        public void run() {
            LogUtil.memoryLog.info("[long-pulling] client count " + allSubs.size());
        }
    }
    
    // =================

    class ClientLongPulling implements Runnable {
        
        @Override
        public void run() {            
            HttpServletRequest request = (HttpServletRequest) asyncContext.getRequest();
            HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
            List<String> changedGroups = ConfigController.compareMd5(request, response,
                    clientMd5Map);
            
            if (changedGroups.size() > 0) {
                sendResponse(changedGroups, System.currentTimeMillis());
                LogUtil.clientLog.info("{}|{}|{}|{}|{}|{}|{}",
                        new Object[]{ (System.currentTimeMillis() - createTime),
                                "instant", RequestUtil.getRemoteIp(request), "polling",
                                clientMd5Map.size(), probeRequestSize, changedGroups.size()});
                return;
            }
            
            asyncTimeoutFuture = scheduler.schedule(new Runnable() {
                public void run() {
                    allSubs.remove(ClientLongPulling.this); // 删除订阅关系
                    sendResponse(null, 0L);
                    LogUtil.clientLog.info("{}|{}|{}|{}|{}|{}",
                            new Object[]{ (System.currentTimeMillis() - createTime),
                                    "timeout", RequestUtil.getRemoteIp((HttpServletRequest) asyncContext.getRequest()), "polling",
                                    clientMd5Map.size(), probeRequestSize});
                }
            }, getLongPullingTimeout(request), TimeUnit.MILLISECONDS);
            
            allSubs.add(this);
        }

        void sendResponse(List<String> changedGroups, long changeTime) {
            if (null != asyncTimeoutFuture) { // 取消超时任务
                asyncTimeoutFuture.cancel(false);
            }

            generateResponse(changedGroups, changeTime);
            asyncContext.complete(); // 告诉容器发送HTTP响应
        }
        
        void generateResponse(List<String> changedGroups, long changeTime) {
            HttpServletRequest request = (HttpServletRequest) asyncContext.getRequest();
            HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();

            //String clientIp = RequestUtil.getRemoteIp(request);
            //long now = System.currentTimeMillis();

            if (null == changedGroups) {
                //pullLog.info("[long-pulling] " + clientIp + ", timeout delayMs " + (now - createTime));
                return;
            }

            try {
                String respString = ConfigController.compareMd5ResultString(changedGroups);
                request.setAttribute("content", respString);

                // 禁用缓存
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader("Expires", 0);
                response.setHeader("Cache-Control", "no-cache,no-store");

                ConfigServlet.forwardErrorPage(request, response, HttpServletResponse.SC_OK);

                /*
                pullLog.info("[long-pulling] send response to " + clientIp + ", delayMs="
                        + (now - createTime) + ", notifyDelay=" + (now - changeTime));
                */
            } catch (Exception se) {
                LogUtil.pullLog.error(se.toString(), se);
            }
        }
        
        ClientLongPulling(AsyncContext ac, Map<String, String> clientMd5Map, int probeRequestSize) {
            this.asyncContext = ac;
            this.clientMd5Map = clientMd5Map;
            this.probeRequestSize = probeRequestSize;
            this.createTime = System.currentTimeMillis();
        }

        // =================
        
        final AsyncContext asyncContext;
        final Map<String, String> clientMd5Map;
        final long createTime;
        final int probeRequestSize;
        
        Future<?> asyncTimeoutFuture;
    }
}

