package com.le.diamond.server.service;

import com.le.diamond.domain.SubscriberStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * ���ٿͻ���md5�ķ��� һ��ʱ��û�бȽ�md5�󣬾�ɾ��IP��Ӧ�ļ�¼��
 */
public class ClientTrackService {
    /**
     * ���ٿͻ���md5.
     */
    static public void trackClientMd5(String ip, Map<String, String> clientMd5Map) {
        ClientRecord record = getClientRecord(ip);
        record.lastTime = System.currentTimeMillis();
        record.groupKey2md5Map.putAll(clientMd5Map);
    }

    static public void trackClientMd5(String ip, Map<String, String> clientMd5Map, Map<String, Long> clientlastPollingTSMap) {
        ClientRecord record = getClientRecord(ip);
        record.lastTime = System.currentTimeMillis();
        record.groupKey2md5Map.putAll(clientMd5Map);
        record.groupKey2pollingTsMap.putAll(clientlastPollingTSMap);
    }
    
    static public void trackClientMd5(String ip, String groupKey, String clientMd5) {
        ClientRecord record = getClientRecord(ip);
        record.lastTime = System.currentTimeMillis();
        record.groupKey2md5Map.put(groupKey, clientMd5);
        record.groupKey2pollingTsMap.put(groupKey, record.lastTime);
    }
    
    
    /**
     * ���ض����߿ͻ��˸���
     */
    static public int subscribeClientCount() {
        return clientRecords.size();
    }
    
    /**
     * �������ж����߸���
     */
    static public long subscriberCount() {
        long count = 0;
        for (ClientRecord record : clientRecords.values()) {
            count += record.groupKey2md5Map.size();
        }
        return count;
    }

    /**
     * groupkey ->  SubscriberStatus
     */
    static public Map<String, SubscriberStatus> listSubStatus(String ip){
        Map<String, SubscriberStatus> status = new HashMap<String, SubscriberStatus>();

        ClientRecord record = getClientRecord(ip);
        if(record == null) return status;

        for (Map.Entry<String, String> entry : record.groupKey2md5Map.entrySet()) {
            String groupKey = entry.getKey();
            String clientMd5 = entry.getValue();
            long lastPollingTs = record.groupKey2pollingTsMap.get(groupKey);
            boolean isUpdate = ConfigService.isUptodate(groupKey, clientMd5);

            status.put(groupKey, new SubscriberStatus(groupKey, isUpdate, clientMd5, lastPollingTs));
        }

        return status;
    }

    /**
     * ip ->  SubscriberStatus
     */
    static public Map<String, SubscriberStatus> listSubsByGroup(String groupKey) {
        Map<String, SubscriberStatus> subs = new HashMap<String, SubscriberStatus>();

        for (ClientRecord clientRec : clientRecords.values()) {
            String clientMd5 = clientRec.groupKey2md5Map.get(groupKey);
            Long lastPollingTs = clientRec.groupKey2pollingTsMap.get(groupKey);

            if (null != clientMd5 && lastPollingTs != null) {
                Boolean isUpdate = ConfigService.isUptodate(groupKey, clientMd5);
                subs.put(clientRec.ip, new SubscriberStatus(groupKey, isUpdate, clientMd5, lastPollingTs));
            }

        }
        return subs;
    }
    
    /**
     * ָ��������IP�����������Ƿ����¡� groupKey -> isUptodate
     */
    static public Map<String, Boolean> isClientUptodate(String ip) {
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        for (Map.Entry<String, String> entry : getClientRecord(ip).groupKey2md5Map.entrySet()) {
            String groupKey = entry.getKey();
            String clientMd5 = entry.getValue();
            Boolean isuptodate = ConfigService.isUptodate(groupKey, clientMd5);
            result.put(groupKey, isuptodate);
        }
        return result;
    }
    
    /**
     * ָ��groupKey���������ж������Լ������Ƿ����¡� IP -> isUptodate
     */
    static public Map<String, Boolean> listSubscriberByGroup(String groupKey) {
        Map<String, Boolean> subs = new HashMap<String, Boolean>();
        
        for (ClientRecord clientRec : clientRecords.values()) {
            String clientMd5 = clientRec.groupKey2md5Map.get(groupKey);
            if (null != clientMd5) {
                Boolean isuptodate = ConfigService.isUptodate(groupKey, clientMd5);
                subs.put(clientRec.ip, isuptodate);
            }
        }
        return subs;
    }
    
    /**
     * �ҵ�ָ��clientIp��Ӧ�ļ�¼��
     */
    static private ClientRecord getClientRecord(String clientIp) {
        ClientRecord record = clientRecords.get(clientIp);
        if (null != record) {
            return record;
        }
        clientRecords.putIfAbsent(clientIp, new ClientRecord(clientIp));
        return clientRecords.get(clientIp);
    }

    static public void refreshClientRecord(){
        clientRecords = new ConcurrentHashMap<String, ClientRecord>();
    }
    // =================

    // ���пͻ��˼�¼������ >> ����/ɾ��
    static volatile ConcurrentMap<String, ClientRecord> clientRecords = new ConcurrentHashMap<String, ClientRecord>();
}

/**
 * ����ͻ��������ݵļ�¼��
 */
class ClientRecord {
    final String ip;
    volatile long lastTime;
    final ConcurrentMap<String, String> groupKey2md5Map;
    final ConcurrentMap<String, Long> groupKey2pollingTsMap;


    ClientRecord(String clientIp) {
        ip = clientIp;
        groupKey2md5Map = new ConcurrentHashMap<String, String>(20, 0.75f, 1);
        groupKey2pollingTsMap = new ConcurrentHashMap<String, Long>(20, 0.75f, 1);
    }
}


