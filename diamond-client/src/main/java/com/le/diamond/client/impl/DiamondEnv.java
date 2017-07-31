package com.le.diamond.client.impl;

import com.le.diamond.manager.ManagerListener;
import com.le.diamond.mockserver.MockServer;
import com.le.diamond.client.BatchHttpResult;
import com.le.diamond.common.Constants;
import com.le.diamond.common.GroupKey;
import com.le.diamond.domain.ConfigInfoEx;
import com.le.diamond.utils.ContentUtils;
import com.le.diamond.utils.JSONUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;



public class DiamondEnv {

    public void addListeners(String dataId, String group, List<? extends ManagerListener> listeners) {
        group = null2defaultGroup(group);

        CacheData cache = addCacheDataIfAbsent(dataId, group);
        for (ManagerListener listener : listeners) {
            cache.addListener(listener);
        }
    }
    
    public void removeListener(String dataId, String group, ManagerListener listener) {
        group = null2defaultGroup(group);

        CacheData cache = getCache(dataId, group);
        if (null != cache) {
            cache.removeListener(listener);
            if (cache.getListeners().isEmpty()) {
                removeCache(dataId, group);
            }
        }
    }
    
    public List<ManagerListener> getListeners(String dataId, String group) {
        group = null2defaultGroup(group);
        
        CacheData cache = getCache(dataId, group);
        if (null == cache) {
            return Collections.emptyList();
        }

        return cache.getListeners();
    }
    
    /**
     * ���ձ������� -> server -> ���ػ�������ȼ���ȡ���á���ʱ��λ�Ǻ��롣
     */
    public String getConfig(String dataId, String group, long timeoutMs) throws IOException {
        group = null2defaultGroup(group);

        if (MockServer.isTestMode()) {
            return MockServer.getConfigInfo(dataId, group, this);
        }

        // ����ʹ�ñ�������
        String content = LocalConfigInfoProcessor.getFailover(this, dataId, group);
        if (content != null) {
            log.warn("[get-config] get failover ok, dataId=" + dataId + ", group=" + group
                    + ", config=" + ContentUtils.truncateContent(content));
            return content;
        }

        try {
            return ClientWorker.getServerConfig(this, dataId, group, timeoutMs);
        } catch (IOException ioe) {
            log.warn("[get-config] get server error, dataId:" + dataId + ", group��" + group
                    + ", " + ioe.toString());
        }

        log.warn("[get-config] get snapshot, dataId:" + dataId + ", group��" + group);
        return LocalConfigInfoProcessor.getSnapshot(this, dataId, group);
    }

    
    /**
     * ��ȡ���ݽӿڣ��������û�ȡ���ݵ�˳�������������ȣ���<br>
     * feature��������ѡֵ��<br>
     * Constants.GETCONFIG_LOCAL_SERVER_SNAPSHOT(�����ļ�-> ������ -> ���ػ���)<br>
     * Constants.GETCONFIG_LOCAL_SNAPSHOT_SERVER(�����ļ�-> ���ػ��� -> ������)
     */
    public String getConfig(String dataId, String group, int feature, long timeoutMs) throws IOException{
    	if(feature == Constants.GETCONFIG_LOCAL_SERVER_SNAPSHOT){
    		return getConfig(dataId, group, timeoutMs);
    	}
    	group = null2defaultGroup(group);

        if (MockServer.isTestMode()) {
            return MockServer.getConfigInfo(dataId, group, this);
        }

        String content = LocalConfigInfoProcessor.getFailover(this, dataId, group);
        if (content != null) {
            log.warn("[get-config] get failover ok, dataId=" + dataId + ", group=" + group
                    + ", config=" + ContentUtils.truncateContent(content));
            return content;
        }
        content = LocalConfigInfoProcessor.getSnapshot(this, dataId, group);
        if(StringUtils.isNotEmpty(content)){
        	log.warn("[get-config] get snapshot, dataId:" + dataId + ", group��" + group);
        	return content;
        }
        return ClientWorker.getServerConfig(this, dataId, group, timeoutMs);
    }
    
    
    public boolean publishSingle(String dataId, String group, String content) {
        checkNotNull(dataId, content);
        group = null2defaultGroup(group);

        /*
        if (MockServer.isTestMode()) {
            CacheData cache = addMockDataIfAbsent(dataId, group);
            cache.setContent(content);
            return true;
        } */

        if (MockServer.isTestMode()) {
            MockServer.setConfigInfo(dataId, group, content, this);
            return true;
        }
        
        String url = "/basestone.do?method=syncUpdateAll";
        List<String> params = Arrays.asList("dataId", dataId, "group", group, "content", content);

        HttpSimpleClient.HttpResult result = null;
        try {
            result = agent.httpPost(url, null, params, Constants.ENCODE, POST_TIMEOUT);
        } catch (IOException ioe) {
            log.warn("[publish-single] error, " + dataId + ", " + group + ", msg: "
                    + ioe.toString());
            return false;
        }

        if (HttpURLConnection.HTTP_OK == result.code) {
            log.info("[publish-single] ok. " + dataId + ", " + group);
            return true;
        } else {
            log.warn("[publish-single] error, " + dataId + ", " + group + ", error code: "
                    + result.code + ", " + result.content);
            return false;
        }
    }

    public boolean publishAggr(String dataId, String group, String datumId, String content) {
        checkNotNull(dataId, datumId, content);
        group = null2defaultGroup(group);
        String url = "/datum.do?method=addDatum";
        List<String> params = Arrays.asList("dataId", dataId, "group", group, "datumId", datumId,
                "content", content);

        HttpSimpleClient.HttpResult result = null;
        try {
            result = agent.httpPost(url, null, params, Constants.ENCODE, POST_TIMEOUT);
        } catch (IOException ioe) {
            log.warn("[publish-aggr] error, " + dataId + ", " + group + ", " + datumId + ", msg: "
                    + ioe.toString());
            return false;
        }

        if (HttpURLConnection.HTTP_OK == result.code) {
            log.info("[publish-aggr] ok. " + dataId + ", " + group + ", " + datumId);
            return true;
        } else {
            log.warn("[publish-aggr] error, " + dataId + ", " + group + ", " + datumId
                    + ", error code: " + result.code + ", " + result.content);
            return false;
        }
    }

    public boolean removeAggr(String dataId, String group, String datumId) {
        checkNotNull(dataId, datumId);
        group = null2defaultGroup(group);
        String url = "/datum.do?method=deleteDatum";
        List<String> params = Arrays.asList("dataId", dataId, "group", group, "datumId", datumId);

        HttpSimpleClient.HttpResult result = null;
        try {
            result = agent.httpPost(url, null, params, Constants.ENCODE, POST_TIMEOUT);
        } catch (IOException ioe) {
            log.warn("[remove-aggr] error, " + dataId + ", " + group + ", " + datumId + ", msg: "
                    + ioe.toString());
            return false;
        }

        if (HttpURLConnection.HTTP_OK == result.code) {
            log.info("[remove-aggr] ok. " + dataId + ", " + group + ", " + datumId);
            return true;
        } else {
            log.warn("[remove-aggr] error, " + dataId + ", " + group + ", " + datumId
                    + ", error code: " + result.code + ", " + result.content);
            return false;
        }
    }

    public boolean remove(String dataId, String group) {
        checkNotNull(dataId);
        group = null2defaultGroup(group);

        /*
        if (MockServer.isTestMode()) {
            CacheData cache = getMockCache(dataId, group);
            if(cache!=null)
            	cache.setContent(null);
            return true;
        } */
        if (MockServer.isTestMode()) {
            MockServer.removeConfigInfo(dataId, group, this);
            return true;
        }
        
        String url = "/datum.do?method=deleteAllDatums";
        List<String> params = Arrays.asList("dataId", dataId, "group", group);

        HttpSimpleClient.HttpResult result = null;
        try {
            result = agent.httpPost(url, null, params, Constants.ENCODE, POST_TIMEOUT);
        } catch (IOException ioe) {
            log.warn("[remove] error, " + dataId + ", " + group + ", msg: " + ioe.toString());
            return false;
        }

        if (HttpURLConnection.HTTP_OK == result.code) {
            log.info("[remove] ok. " + dataId + ", " + group);
            return true;
        } else {
            log.warn("[remove] error, " + dataId + ", " + group + ", error code: " + result.code
                    + ", " + result.content);
            return false;
        }
    }

    public List<String> getServerUrls() {
        return new ArrayList<String>(serverMgr.serverUrls);
    }
    
    private void checkNotNull(String... params) {
        for (String param : params) {
            if (StringUtils.isBlank(param)) {
                throw new IllegalArgumentException("param cannot be blank");
            }
        }
    }
    
    private String null2defaultGroup(String group) {
        return (null == group) ? Constants.DEFAULT_GROUP : group.trim();
    }

    /**
     * ������ѯ���õ�server snapshotֵ
     */
    public BatchHttpResult<ConfigInfoEx> batchGetConfig(List<String> dataIds, String group, long timeoutMs) {
        // check parameters
        if (dataIds == null) {
            throw new IllegalArgumentException("dataId list is null when batch get config");
        }

        group = null2defaultGroup(group);

        if(MockServer.isTestMode()){
            List<ConfigInfoEx> result = MockServer.batchQuery(dataIds, group, this);
            BatchHttpResult<ConfigInfoEx> response = new BatchHttpResult<ConfigInfoEx>(true, HttpURLConnection.HTTP_OK, "", "mock server");
            response.getResult().addAll(result);
            return response;
        }

        StringBuilder dataIdstr = new StringBuilder();
        String split = "";
        for (String dataId : dataIds) {
            dataIdstr.append(split);
            dataIdstr.append(dataId);
            split = Constants.WORD_SEPARATOR;
        }

        // fire http request
        String url = "/config.co?method=batchGetConfig";
        List<String> params = Arrays.asList("dataIds", dataIdstr.toString(), "group", group);
        HttpSimpleClient.HttpResult result = null;
        try {
            result = agent.httpPost(url, null, params, Constants.ENCODE, timeoutMs);
        } catch (IOException ioe) { // ��������ʧ��
            log.warn("[batch-get-config] error, " + dataIds + ", " + group, ioe);
            return new BatchHttpResult<ConfigInfoEx>(false, -1, "batch get config io exception:" + ioe.getMessage(), "");
        }

        // prepare response
        BatchHttpResult<ConfigInfoEx> response = new BatchHttpResult<ConfigInfoEx>(true, result.code, "", result.content);

        // handle http code
        if(result.code == HttpURLConnection.HTTP_OK){ // http code 200
            response.setSuccess(true);
            response.setStatusMsg("batch get config success");
            log.info("[batch-get-config] success, " + ",dataIds=" + dataIds + ",group=" + group);
        } else { // http code: 412 500
            response.setSuccess(false);
            response.setStatusMsg("batch get config fail, status:" + result.code);
            log.error("[batch-get-config] fail, status:" + result.code + ",dataIds=" + dataIds + ",group=" + group);
        }

        // deserialize batch query result items
        if (HttpURLConnection.HTTP_OK == result.code ||
                HttpURLConnection.HTTP_PRECON_FAILED == result.code) {
            try {
                String json = result.content;
                Object resultObj = JSONUtils.deserializeObject(json,
                        new TypeReference<List<ConfigInfoEx>>() {
                        });
                response.getResult().addAll((List<ConfigInfoEx>) resultObj);
            } catch (Exception e) {  // ������Ӧʧ��
                response.setSuccess(false);
                response.setStatusMsg("batch get config deserialize error");
                log.error("[batch-get-config] deserialize error, " + ",dataIds=" + dataIds + ",group=" + group, e);
            }
        }

        return response;
    }

    /**
     * ������ѯ���õ�dbֵ
     */
    @SuppressWarnings("unchecked")
    public BatchHttpResult<ConfigInfoEx> batchQuery(List<String> dataIds, String group,
            long timeoutMs) {

        // �������ؽ��
        BatchHttpResult<ConfigInfoEx> response = new BatchHttpResult<ConfigInfoEx>();

        // �ж�list�Ƿ�Ϊnull
        if (dataIds == null) {
            throw new IllegalArgumentException("dataId list is null when batch query");
        }

        group = null2defaultGroup(group);

        if(MockServer.isTestMode()){
            List<ConfigInfoEx> result = MockServer.batchQuery(dataIds, group, this);
            response.setStatusCode(HttpURLConnection.HTTP_OK);
            response.setResponseMsg("mock server");
            response.setSuccess(true);
            response.getResult().addAll(result);
            return response;
        }

        // ��dataId��list����Ϊ��һ�����ɼ��ַ��ָ����ַ���
        StringBuilder dataIdstr = new StringBuilder();
        String split = "";
        for (String dataId : dataIds) {
            dataIdstr.append(split);
            dataIdstr.append(dataId);
            split = Constants.WORD_SEPARATOR;
        }

        String url = "/admin.do?method=batchQuery";
        List<String> params = Arrays.asList("dataIds", dataIdstr.toString(), "group", group);

        HttpSimpleClient.HttpResult result = null;
        try {
            result = agent.httpPost(url, null, params, Constants.ENCODE, timeoutMs);
        } catch (IOException ioe) {
            log.warn("[batch-query] error, " + dataIds + ", " + group, ioe);
            response.setSuccess(false);
            response.setStatusMsg("batch query io exception��" + ioe.getMessage());
            return response;
        }

        response.setStatusCode(result.code);
        response.setResponseMsg(result.content);

        // error result code
        if (HttpURLConnection.HTTP_OK == result.code ||
                HttpURLConnection.HTTP_PRECON_FAILED == result.code) {

            try {
                String json = result.content;
                Object resultObj = JSONUtils.deserializeObject(json,
                        new TypeReference<List<ConfigInfoEx>>() {
                        });
                response.setSuccess(true);
                response.getResult().addAll((List<ConfigInfoEx>) resultObj);
                log.info("batch query success, " + ",dataIds=" + dataIds + ",group=" + group);
            } catch (Exception e) {
                response.setSuccess(false);
                response.setStatusMsg("batch query deserialize error");
                log.error(
                        "batch query deserialize error, " + ",dataIds=" + dataIds + ",group=" + group,
                        e);
            }


        } else {
            response.setSuccess(false);
            response.setStatusMsg("batch query fail, status:" + result.code);
            log.error("batch query fail, status:" + result.code + ",dataIds=" + dataIds + ",group="
                    + group);
            return response;

        }




        return response;
    }
    
    
    /**
     * ��ѯCacheData������NULL��ʾ�Ҳ�����
     */
    CacheData getCache(String dataId, String group) {
        if (null == dataId || null == group) {
            throw new IllegalArgumentException();
        }
        return cacheMap.get().get(GroupKey.getKey(dataId, group));
    }

    /*
    CacheData getMockCache(String dataId, String group) {
        if (null == dataId || null == group) {
            throw new IllegalArgumentException();
        }
        return mockDataSource.get().get(GroupKey.getKey(dataId, group));
    }
    */
    
    List<CacheData> getAllCacheDataSnapshot() {
        return new ArrayList<CacheData>(cacheMap.get().values());
    }
    
    void removeCache(String dataId, String group) {
        String groupKey = GroupKey.getKey(dataId, group);
        synchronized (cacheMap) {
            Map<String, CacheData> copy = new HashMap<String, CacheData>(cacheMap.get());
            copy.remove(groupKey);
            cacheMap.set(copy);
        }
        log.info("[unsubscribe] " + groupKey);
    }
    
    /**
     * ��ѯCacheData��������ʱ������
     */
    public CacheData addCacheDataIfAbsent(String dataId, String group) {
        CacheData cache = getCache(dataId, group);
        if (null != cache) {
            return cache;
        }

        synchronized (cacheMap) {
            String key = GroupKey.getKey(dataId, group);
            cache = new CacheData(dataId, group);

            Map<String, CacheData> copy = new HashMap<String, CacheData>(cacheMap.get());
            copy.put(key, cache);
            cacheMap.set(copy);
            log.info("[subscribe] " + key);
        }

		String content = LocalConfigInfoProcessor.getFailover(this, dataId, group);
		content = (null != content) ? content //
				: LocalConfigInfoProcessor.getSnapshot(this, dataId, group);
		cache.setContent(content);
        return cache;
    }
    
    
    /**

    public CacheData addMockDataIfAbsent(String dataId, String group) {
        CacheData cache = getMockCache(dataId, group);
        if (null != cache) {
            return cache;
        }
        
        Map<String, CacheData> map = mockDataSource.get();
        String key = GroupKey.getKey(dataId, group);
        cache = new CacheData(dataId, group);
        map.put(key, cache);
        
        if (this == DiamondEnvRepo.defaultEnv) {
            String content = LocalConfigInfoProcessor.getFailover(this, dataId, group);
            content = (null != content) ? content 
                    : LocalConfigInfoProcessor.getSnapshot(this, dataId, group);
            cache.setContent(content);
        }
        
        return cache;
    }
     */
    
    /**
     * �������ж���dataId��ֻ�����ϡ�
     */
    public Set<String> getSubscribeDataIds() {
        Map<String, CacheData> cacheMapSnapshot = cacheMap.get();
        
        Set<String> dataIds = new HashSet<String>(cacheMapSnapshot.size());
        for (CacheData cache : cacheMapSnapshot.values()) {
            dataIds.add(cache.dataId);
        }
        return dataIds;
    }
    
    
    @Override
    public String toString() {
        return "DiamondEnv-" + serverMgr.toString();
    }

    public ServerListManager getServerMgr() {
        return serverMgr;
    }

    public void initServerManager(ServerListManager _serverMgr) {
    	_serverMgr.setEnv(this);
        serverMgr = _serverMgr;
        serverMgr.start();
        agent = new ServerHttpAgent(serverMgr);
    }

    public DiamondEnv(String... serverIps) {
        this(new ServerListManager(Arrays.asList(serverIps)));
    }
    
    protected DiamondEnv(ServerListManager serverListMgr) {
    	//serverListMgr.setEnv(this);
        initServerManager(serverListMgr);
        cacheMap = new AtomicReference<Map<String, CacheData>>(new HashMap<String, CacheData>());
        //mockDataSource = new AtomicReference<Map<String, CacheData>>(new HashMap<String, CacheData>());
        worker = new ClientWorker(this);
    }
    
    /*
    public Map<String, CacheData> getMockDataSource(){
    	return mockDataSource.get();
    }
    
    public void setMockDataSource(Map<String, CacheData> cache){
    	mockDataSource.set(cache);
    }
     */
    
    // =====================

    static final public Logger log = Logger.getLogger(DiamondEnv.class);
    static public final long POST_TIMEOUT = 3000L;
    
    static final String selfIp;
    static {
        String tmp = null;
        try {
            tmp = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        selfIp = tmp;
    }
    
    protected ServerListManager serverMgr;
    protected ServerHttpAgent agent; // �����server��ϵ
    protected ClientWorker worker ;
    
    final private AtomicReference<Map<String/* groupKey */, CacheData>> cacheMap; // COW����
    //final private AtomicReference<Map<String/* groupKey */, CacheData>> mockDataSource;

}
