package com.le.diamond.client;

import com.le.diamond.client.impl.DiamondEnv;
import com.le.diamond.client.impl.DiamondEnvRepo;
import com.le.diamond.client.impl.LocalConfigInfoProcessor;
import com.le.diamond.client.impl.LogConstants;
import com.le.diamond.manager.ManagerListener;
import com.le.diamond.domain.ConfigInfoEx;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * �ͻ��˹��ܣ�
 * <ul>
 * <li>����/ֱ��������
 * <li>��������
 * <li>ͬ��������
 * <li>���������ļ�
 * <li>���ػ����ļ���ע��windowsϵͳ�ļ�������
 * <li>����ʱ��⻺����Ч��
 * 
 * @author jiuRen
 */
public class Diamond {

    static public void addListener(String dataId, String group, ManagerListener listener) {
        DiamondEnvRepo.defaultEnv.addListeners(dataId, group, Arrays.asList(listener));
    }
    static public void addListeners(String dataId, String group, List<ManagerListener> listeners) {
        DiamondEnvRepo.defaultEnv.addListeners(dataId, group, listeners);
    }

    static public void removeListener(String dataId, String group, ManagerListener listener) {
        DiamondEnvRepo.defaultEnv.removeListener(dataId, group, listener);
    }
    
    static public List<ManagerListener> getListeners(String dataId, String group) {
        return DiamondEnvRepo.defaultEnv.getListeners(dataId, group);
    }
    
    /**
     * ���ձ������� -> server -> ���ػ�������ȼ���ȡ���á���ʱ��λ�Ǻ��롣
     */
    static public String getConfig(String dataId, String group, long timeoutMs) throws IOException {
        return DiamondEnvRepo.defaultEnv.getConfig(dataId, group, timeoutMs);
    }
    
    /**
     * ��ȡ���ݽӿڣ��������û�ȡ���ݵ�˳�������������ȣ���<br>
     * feature��������ѡֵ��<br>
     * Constants.GETCONFIG_LOCAL_SERVER_SNAPSHOT(�����ļ�-> ������ -> ���ػ���)<br>
     * Constants.GETCONFIG_LOCAL_SNAPSHOT_SERVER(�����ļ�-> ���ػ��� -> ������)
     */
    static public String getConfig(String dataId, String group, int feature, long timeoutMs) throws IOException{
    	return DiamondEnvRepo.defaultEnv.getConfig(dataId, group, feature, timeoutMs);
    }

    static public boolean publishSingle(String dataId, String group, String content) {
        return DiamondEnvRepo.defaultEnv.publishSingle(dataId, group, content);
    }

    static public boolean publishAggr(String dataId, String group, String datumId, String content) {
        return DiamondEnvRepo.defaultEnv.publishAggr(dataId, group, datumId, content);
    }

    static public boolean remove(String dataId, String group) {
        return DiamondEnvRepo.defaultEnv.remove(dataId, group);
    }

    static public boolean removeAggr(String dataId, String group, String datumId) {
        return DiamondEnvRepo.defaultEnv.removeAggr(dataId, group, datumId);
    }

    static public DiamondEnv getTargetEnv(String... serverIps) {
        return DiamondEnvRepo.getTargetEnv(serverIps);
    }
    
    static public List<DiamondEnv> allDiamondEnvs() {
        return DiamondEnvRepo.allDiamondEnvs();
    }

    /**
     * ������ѯ�����ص�{@link BatchHttpResult}�а�����{@link ConfigInfoEx}���б������ѯʧ�ܣ����ߴ���Ӧ��ԭ
     * {@link ConfigInfoEx}����ʧ�ܣ��򷵻�ʧ�ܵ�{@link BatchHttpResult}����ѯ�ɹ�ʱ��
     * {@link ConfigInfoEx#getStatus()}
     * ��ʾ�����Ƿ���ڣ�1��ʾ���ڣ�2��ʾ�����ڣ�-1��ʾ��ѯ���ݿⷢ���쳣��ʹ��������Ҫ��Ϊ�˼���ԭ�е�SDK�ӿڡ�
     * 
     * @param dataIds
     *            Ҫ��ѯ��dataId�б�
     * @param group
     *            ����
     * @param timeoutMs
     *            ��ʱʱ��
     * @return {@link BatchHttpResult}����֤��ΪNULL.
     */
    static public BatchHttpResult<ConfigInfoEx> batchQuery(List<String> dataIds, String group,
            long timeoutMs) {
        return DiamondEnvRepo.defaultEnv.batchQuery(dataIds, group, timeoutMs);
    }

    static public BatchHttpResult<ConfigInfoEx> batchGetConfig(List<String> dataIds, String group,
                                                           long timeoutMs) {
        return DiamondEnvRepo.defaultEnv.batchGetConfig(dataIds, group, timeoutMs);
    }
    


    static private void initLog() throws Exception {
    	
        final String logPath = LocalConfigInfoProcessor.getLogFile();
        System.out.println("diamond client log path : " + new File(logPath).getAbsolutePath());

        FileAppender appender = new DailyRollingFileAppender();
        appender.setAppend(true);
        appender.setEncoding("GBK");
        appender.setFile(logPath);
        appender.setLayout(new PatternLayout("%d %-5p - %m%n%n"));
        appender.activateOptions();

        DiamondEnv.log.setLevel(Level.INFO);
        DiamondEnv.log.setAdditivity(false);
        DiamondEnv.log.addAppender(appender);
    }

    static private void checkSnapshotValidity() {
        List<String> localServerlist = LocalConfigInfoProcessor.readServerlist(DiamondEnvRepo.defaultEnv);
        List<String> apacheServerlist = DiamondEnvRepo.defaultEnv.getServerUrls();
        DiamondEnv.log.info("[apache-urls] " + apacheServerlist);
        DiamondEnv.log.info("[cache-urls] " + localServerlist);

        boolean isNotChange = apacheServerlist.equals(localServerlist);
        if (isNotChange) {
            DiamondEnv.log.info(LogConstants.PREFFIX + "environment ok.");
        } else {
            DiamondEnv.log.warn(LogConstants.PREFFIX + "environment changed. clear cache.");
            LocalConfigInfoProcessor.cleanAllSnapshot();
            LocalConfigInfoProcessor.saveServerlist(DiamondEnvRepo.defaultEnv, apacheServerlist);
        }
    }


    // ==========================
    static {
        try {
            //
            initLog();
            //
            checkSnapshotValidity();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
