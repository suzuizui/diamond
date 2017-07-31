package com.le.diamond.server.service.acl;

import com.le.diamond.md5.MD5;
import com.le.diamond.server.utils.LogUtil;
import com.le.diamond.server.utils.RequestUtil;
import com.le.diamond.server.service.ServerListService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * le.com Inc. Copyright (c) 1998-2101 All Rights Reserved.
 * <p/>
 * Project: diamond-server
 * User: qiaoyi.dingqy
 * Date: 13-11-13
 * Time: 下午7:50
 */
//@Service
public class ACLService {
    public static final String ACL_REGISTERAPP_DATAID = "com.le.diamond.meta.acl.registerapp";
    public static final String ACL_TRUSTIPS_DATAID = "com.le.diamond.meta.acl.trustips";

    // appName -> appKey
    private static volatile Map<String, String> apps = new HashMap<String, String>();
    private static volatile Set<String> trustIps = new HashSet<String>();

    private ServerListService serverListService;

    @Autowired
    public ACLService(ServerListService serverListService) {
        this.serverListService = serverListService;
    }

    static public void loadTrustIps(String content) {
        if (StringUtils.isBlank(content)) {
            LogUtil.aclLog.error("[loadTrustIps] trust ip is blank.");
            return;
        }

        LogUtil.aclLog.warn("[loadTrustIps] trust ip meta config:{}", content);

        try {
            List<String> lines = IOUtils.readLines(new StringReader(content));
            Set<String> list = new HashSet<String>(lines.size());
            for (String line : lines) {
                if (!StringUtils.isBlank(line) && !line.startsWith("#")) {
                    list.add(line.trim());
                }
            }
            trustIps = list;
        } catch (Exception ioe) {
            LogUtil.aclLog.error("[loadTrustIps] failed to load trust ips, " + ioe.toString(), ioe);
        }
    }

    //  com.le.diamond.meta.acl.registerapp 元配置dump时更新ACLService内存数据
    static public void loadRegisterApp(String content) {
        if (StringUtils.isBlank(content)) {
            LogUtil.aclLog.error("register apps is blank.");
            return;
        }
        LogUtil.aclLog.warn("[register apps] {}", content);
        try {
            apps = load(content);
        } catch (Exception ioe) {
            LogUtil.aclLog.error("failed to load blacklist, " + ioe.toString(), ioe);
        }
    }

    static private Map<String, String> load(String content) throws IOException {
        List<String> lines = IOUtils.readLines(new StringReader(content));
        Map<String, String> map = new HashMap<String, String>(lines.size());
        for (String line : lines) {
            if (!StringUtils.isBlank(line) && !line.startsWith("#")) {
                String[] info = line.split("\\s+");
                if (info == null || info.length != 2) {
                    LogUtil.aclLog.error("corrupt app record {} ", line);
                    continue;
                }
                map.put(info[0].trim(), info[1].trim());
            }
        }
        return map;
    }

    // trust ips from meta config && server node ips
    public boolean isTrustRequester(HttpServletRequest request){
        String requestIp = RequestUtil.getRemoteIp(request);

        return serverListService.getServerList().contains(requestIp) // for server
                || trustIps.contains(requestIp) // for ops
                || "127.0.0.1".equals(requestIp); // for test
    }

    // trust token == MD5(timestamp + appKey)
    public boolean checkIdentity(String appName, String timestamp, String token) {
        // lack of identity information, return false
        if (StringUtils.isBlank(appName) || StringUtils.isBlank(timestamp) || StringUtils.isBlank(token)) {
            LogUtil.aclLog.error("[acl-check-identity] error, lacking identity info. appName:{}, timestamp:{}, token:{}",
                    new Object[]{appName, timestamp, token});
            return false;
        }

        String appKey = apps.get(appName);
        if (appKey == null) {
            LogUtil.aclLog.error("[acl-check-identity] error, unregister app. appName:{}", appName);
            return false;
        }

        // check identity
        String trust = MD5.getInstance().getMD5String(timestamp + appKey);

        if (!trust.equals(token)) { // failed
            LogUtil.aclLog.error("[acl-check-identity] error, check app token failed. appName:{}, appKey:{}, timestamp:{}, trust:{}, token:{}",
                    new Object[]{appName, appKey, timestamp, trust, token});
            return false;
        }

        return true; // success
    }
}
