package com.le.diamond.client.impl;

import com.le.diamond.common.Constants;
import com.le.diamond.md5.MD5;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;


public class HttpSimpleClient {

//    public static void main(String[] args) throws Exception {
//        HttpURLConnection conn = null;
//        try {
//            conn = (HttpURLConnection) new URL("http://jmenv.tbsite.net:8082/siteip").openConnection();
//            conn.setRequestMethod("GET");
//            conn.setConnectTimeout(3000);
//            conn.setReadTimeout(3000);
//            
//            conn.connect();
//            int respCode = conn.getResponseCode(); // �����ڲ���������
//            String resp = null;
//
//            if (HttpURLConnection.HTTP_OK == respCode) {
//                resp = IOUtils.toString(conn.getInputStream());
//            } else {
//                resp = IOUtils.toString(conn.getErrorStream());
//            }
//            System.out.println(respCode);
//            System.out.println(resp);
//        } finally {
//            if (conn != null) {
//                conn.disconnect();
//            }
//        }
//    }
    /**
     * ����GET����
     */
    static public HttpResult httpGet(String url, List<String> headers, List<String> paramValues,
            String encoding, long readTimeoutMs) throws IOException {
        String encodedContent = encodingParams(paramValues, encoding);
        url += (null == encodedContent) ? "" : ("?" + encodedContent);
        
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(100);
            conn.setReadTimeout((int) readTimeoutMs);
            setHeaders(conn, headers, encoding);

            conn.connect();
            int respCode = conn.getResponseCode(); // �����ڲ���������
            String resp = null;

            if (HttpURLConnection.HTTP_OK == respCode) {
                resp = IOUtils.toString(conn.getInputStream(), encoding);
            } else {
                resp = IOUtils.toString(conn.getErrorStream(), encoding);
            }
            return new HttpResult(respCode, resp);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * ����POST����
     * 
     * @param url
     * @param headers
     *            ����Header������Ϊnull
     * @param paramValues
     *            ����������Ϊnull
     * @param encoding
     *            URL����ʹ�õ��ַ���
     * @param readTimeoutMs
     *            ��Ӧ��ʱ
     * @return
     * @throws IOException
     */
    static public HttpResult httpPost(String url, List<String> headers, List<String> paramValues,
            String encoding, long readTimeoutMs) throws IOException {
        String encodedContent = encodingParams(paramValues, encoding);
        
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout((int) readTimeoutMs);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            setHeaders(conn, headers, encoding);

            conn.getOutputStream().write(encodedContent.getBytes());

            int respCode = conn.getResponseCode(); // �����ڲ���������
            String resp = null;

            if (HttpURLConnection.HTTP_OK == respCode) {
                resp = IOUtils.toString(conn.getInputStream(), encoding);
            } else {
                resp = IOUtils.toString(conn.getErrorStream(), encoding);
            }
            return new HttpResult(respCode, resp);
        } finally {
            if (null != conn) {
                conn.disconnect();
            }
        }
    }

    static private void setHeaders(HttpURLConnection conn, List<String> headers, String encoding) {
        if (null != headers) {
            for (Iterator<String> iter = headers.iterator(); iter.hasNext();) {
                conn.addRequestProperty(iter.next(), iter.next());
            }
        }
        conn.addRequestProperty("Client-Version", "3.6.8"); // TODO
        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset="
                + encoding);

        //
        String ts = String.valueOf(System.currentTimeMillis());
        String token = MD5.getInstance().getMD5String(ts + ServerHttpAgent.appKey);

        conn.addRequestProperty(Constants.CLIENT_APPNAME_HEADER, ServerHttpAgent.appName);
        conn.addRequestProperty(Constants.CLIENT_REQUEST_TS_HEADER, ts);
        conn.addRequestProperty(Constants.CLIENT_REQUEST_TOKEN_HEADER, token);
    }

    static private String encodingParams(List<String> paramValues, String encoding)
            throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        if (null == paramValues) {
            return null;
        }
        
        for (Iterator<String> iter = paramValues.iterator(); iter.hasNext();) {
            sb.append(iter.next()).append("=");
            sb.append(URLEncoder.encode(iter.next(), encoding));
            if (iter.hasNext()) {
                sb.append("&");
            }
        }
        return sb.toString();
    }
    
    
    
    static public class HttpResult {
        final public int code;
        final public String content;

        public HttpResult(int code, String content) {
            this.code = code;
            this.content = content;
        }
    }
}
