package com.le.diamond.client.impl;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.List;

/**
 * �������ȼ�˳���server������ֱ��ĳ��server����ɹ���
 * 
 * ճ�ԣ��������ĳ��server�ɹ�����ô�¸�����Ҳ�����ȷ�����server��
 */
class ServerHttpAgent {
    public static final String appKey;
    public static final String appName;

    static {
        // �ͻ��������Ϣ
        appKey = System.getProperty("diamond.client.appKey", "");
        appName = System.getProperty("diamond.client.appName", "");
    }

    ServerHttpAgent(ServerListManager mgr) {
        serverListMgr = mgr;
    }

    /**
     * @param path
     *            �����webӦ�ø�����/��ͷ
     * @param headers
     * @param paramValues
     * @param encoding
     * @param readTimeoutMs
     * @return
     * @throws IOException
     */
    public HttpSimpleClient.HttpResult httpGet(String path, List<String> headers, List<String> paramValues,
                                               String encoding, long readTimeoutMs) throws IOException {
        final long endTime = System.currentTimeMillis() + readTimeoutMs;

        if (null != currentServerIp) {
            try {
                HttpSimpleClient.HttpResult result = HttpSimpleClient.httpGet(getUrl(currentServerIp, path),
                        headers, paramValues, encoding, readTimeoutMs);
                return result;
            } catch (ConnectException ce) {
            } catch (SocketTimeoutException stoe) {
            } catch (IOException ioe) {
                throw ioe;
            }
        }

        for (Iterator<String> serverIter = serverListMgr.iterator(); serverIter.hasNext();) {
            long timeout = endTime - System.currentTimeMillis();
            if (timeout <= 0) {
                currentServerIp = serverIter.next(); // previous node performs slowly
                throw new IOException("timeout");
            }

            String ip = serverIter.next();
            try {
                HttpSimpleClient.HttpResult result = HttpSimpleClient.httpGet(getUrl(ip, path), headers,
                        paramValues, encoding, timeout);
                currentServerIp = ip;
                return result;
            } catch (ConnectException ce) {
            } catch (SocketTimeoutException stoe) {
            } catch (IOException ioe) {
                throw ioe;
            }
        }
        throw new ConnectException("no available server");
    }

    public HttpSimpleClient.HttpResult httpPost(String path, List<String> headers, List<String> paramValues,
                                                String encoding, long readTimeoutMs) throws IOException {
        final long endTime = System.currentTimeMillis() + readTimeoutMs;

        if (null != currentServerIp) {
            try {
                HttpSimpleClient.HttpResult result = HttpSimpleClient.httpPost(getUrl(currentServerIp, path),
                        headers, paramValues, encoding, readTimeoutMs);
                return result;
            } catch (ConnectException ce) {
            } catch (SocketTimeoutException stoe) {
            } catch (IOException ioe) {
                throw ioe;
            }
        }

        for (Iterator<String> serverIter = serverListMgr.iterator(); serverIter.hasNext();) {
            long timeout = endTime - System.currentTimeMillis();
            if (timeout <= 0) {
                currentServerIp = serverIter.next(); // previous node performs slowly
                throw new IOException("timeout");
            }

            String ip = serverIter.next();
            try {
                HttpSimpleClient.HttpResult result = HttpSimpleClient.httpPost(getUrl(ip, path), headers,
                        paramValues, encoding, timeout);
                currentServerIp = ip;
                return result;
            } catch (ConnectException ce) {
            } catch (SocketTimeoutException stoe) {
            } catch (IOException ioe) {
                throw ioe;
            }
        }
        throw new ConnectException("no available server");
    }

    // relativePath�����webӦ�ø�·������/��ͷ
    static String getUrl(String ip, String relativePath) {
        return "http://" + ip + ":8080/diamond-server" + relativePath;
    }
    
    /**
     * ��Ⱥ�������б��ͱ仯������currentServerIp
     */
    public void reSetCurrentServerIp(){
    	if(currentServerIp!=null)
    		currentServerIp = null;
    }

    // =================
    final ServerListManager serverListMgr;
    volatile String currentServerIp;
}
