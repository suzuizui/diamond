package com.le.diamond.client.impl;

import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.le.diamond.client.impl.DiamondEnv.log;

/**
 * ��Ԫ����������
 * 
 * @author JIUREN
 */
public class DiamondUnitSite {

    /**
     * ��client�Ƿ������Ļ���
     */
    static public boolean isInCenterUnit() {
        return serverMgr_default.getUrlString().equals(serverMgr_center.getUrlString());
    }
    
    /**
     * �л������Ļ�����DIAMOND��Ⱥ��
     */
    static public void switchToCenterUnit() {
        /*
    	if(MockServer.isTestMode()){
    		defaultEnv.setMockDataSource(mockServerCache.get("center"));
    	} */
    	DiamondEnvRepo.defaultEnv.initServerManager(serverMgr_center);
    }
    
    /**
     * �л����Լ����ڵ�Ԫ������DIAMOND��Ⱥ��
     */
    static public void switchToLocalUnit() {
        /*
    	if(MockServer.isTestMode()){
    		defaultEnv.setMockDataSource(mockServerCache.get("default"));
    	} */
        DiamondEnvRepo.defaultEnv.initServerManager(serverMgr_default);
    }
    
    /**
     * �õ����Ļ�����Diamond������
     */
    static public DiamondEnv getCenterUnitEnv() {
    	DiamondEnv env = DiamondEnvRepo.getUnitEnv("center");
        /*
    	if(MockServer.isTestMode()){
    		env.setMockDataSource(mockServerCache.get("center"));
    	}
    	*/
        return env;
    }
    
    /**
     * �õ�ָ����Ԫ��Diamond������
     */
    static public DiamondEnv getDiamondUnitEnv(String unitName) {
    	DiamondEnv env = DiamondEnvRepo.getUnitEnv(unitName);
        /*
    	if(MockServer.isTestMode()){
    		if(unitName.equals("center"))
    			env.setMockDataSource(mockServerCache.get("center"));
    	} */
        return env;
    }

    
    /**
     * �����е�Ԫ���������ĵ�Ԫ��������ͨ���ݡ�
     * 
     * @throws IOException
     *             ��ĳ��Ԫ����ʧ�ܺ��׳��쳣
     */
    static public void publishToAllUnit(String dataId, String group, String content)
            throws IOException {
        for (DiamondEnv env : getUnitList()) {
            if (!env.publishSingle(dataId, group, content)) {
                throw new IOException("pub fail to unit " + env + ", " + dataId + ", " + group);
            }
        }
    }

    /**
     * �����е�Ԫ���������ĵ�Ԫ��ɾ�����ݡ�
     * 
     * @throws IOException
     */
    static public void removeToAllUnit(String dataId, String group) throws IOException {
        for (DiamondEnv env : getUnitList()) {
            if (!env.remove(dataId, group)) {
                throw new IOException("rm fail to unit " + env + ", " + dataId + ", " + group);
            }
        }
    }
    
    /**
     * �õ����е�Ԫ�����б�
     * 
     * @throws IOException  ��ȡ��Ԫ�б�ʱ����.
     */
    static public List<DiamondEnv> getUnitList() throws IOException {
        List<String> unitNameList = null;

        HttpSimpleClient.HttpResult httpResult = HttpSimpleClient.httpGet(
                "http://jmenv.tbsite.net:8080/diamond-server/unit-list?nofix=1", null, null, "GBK",
                1000L);

        if (HttpURLConnection.HTTP_OK == httpResult.code) {
            unitNameList = IOUtils.readLines(new StringReader(httpResult.content));
        } else {
            throw new IOException("http code " + httpResult.code + ", msg: " + httpResult.content);
        }

        if (!unitNameList.contains("center")) {
            unitNameList.add("center");
        }
        List<DiamondEnv> envList = new ArrayList<DiamondEnv>(unitNameList.size());
        for (String unitName : unitNameList) {
            DiamondEnv env = DiamondEnvRepo.getUnitEnv(unitName);
            envList.add(env);
        }
        return envList;
    }

    // ==================

    static private ServerListManager serverMgr_default = new ServerListManager();
    static private ServerListManager serverMgr_center = new ServerManager_unitSite("center");

    // ��Ԫ����ServerManager��ӳ��
    static Map<String, ServerListManager> unit2serverMgr = new HashMap<String, ServerListManager>();
    
    //����default��center�����ݵ�cache
    static public  Map<String, Map<String, CacheData>> mockServerCache = null;
    
    static{
    	//��ʼ���������б�
    	serverMgr_default.initServerList();
    	serverMgr_center.initServerList();

        /*
    	//��ʼ��cache
    	mockServerCache = new HashMap<String, Map<String, CacheData>>();
    	mockServerCache.put("default", DiamondEnvRepo.defaultEnv.getMockDataSource());
    	if(DiamondUnitSite.isInCenterUnit())
    		mockServerCache.put("center", DiamondEnvRepo.defaultEnv.getMockDataSource());
    	else
    		mockServerCache.put("center", new HashMap<String, CacheData>());
        */
    }

}

/**
 * ��Ԫ�������Ӧ��ServerManager�� ���ַ��������ȡ�б�ʱ���Ӳ��� nofix����ʾ����У����
 */
class ServerManager_unitSite extends ServerListManager {

    public ServerManager_unitSite(String unitName) {
        unit = unitName;
        super.name = unitName;
        getServersUrl = "http://jmenv.tbsite.net:8080/diamond-server/diamond-unit-" + unitName
                + "?nofix=1";
    }

    public synchronized void start() {
        if (isStarted || isFixed) {
            return;
        }

        GetServerListTask getServersTask = new GetServerListTask(getServersUrl);
        for (int i = 0; i < 3 && serverUrls.isEmpty(); ++i) {
            getServersTask.run();
            try {
                Thread.sleep(100L);
            } catch (Exception e) {
            }
        }
        if (serverUrls.isEmpty()) {
            log.warn("no diamond in unit: " + unit);
        }
        
        TimerService.scheduleWithFixedDelay(getServersTask, 0L, 30L, TimeUnit.SECONDS);
        isStarted = true;
    }
    
    //��ʼ���������б���������ʱ����
    public void initServerList(){    	   	
        GetServerListTask getServersTask = new GetServerListTask(getServersUrl);
        for (int i = 0; i < 3 && serverUrls.isEmpty(); ++i) {
            getServersTask.run();
            try {
                Thread.sleep(100L);
            } catch (Exception e) {
            }
        }
    }
    
    
    @Override
    public String toString() {
        return "ServerManager-unit-" + unit;
    }

    // ==========================
    final String unit;
    final String getServersUrl;
}
