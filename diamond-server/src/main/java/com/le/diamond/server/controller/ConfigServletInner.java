package com.le.diamond.server.controller;

import com.le.diamond.common.Constants;
import com.le.diamond.domain.ConfigInfoEx;
import com.le.diamond.server.service.ConfigService;
import com.le.diamond.server.utils.LogUtil;
import com.le.diamond.server.service.DiskUtil;
import com.le.diamond.server.service.LongPullingService;
import com.le.diamond.server.service.trace.ConfigTraceService;
import com.le.diamond.server.utils.GroupKey2;
import com.le.diamond.server.utils.RequestUtil;
import com.le.diamond.server.utils.TimeUtil;
import com.le.diamond.utils.JSONUtils;
import com.le.diamond.utils.Protocol;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: dingjoey
 * Date: 13-12-16
 * Time: 19:17
 * <p/>
 * spring aop不能切入servlet的继承方法，所以抽象出此class
 */
@Service
public class ConfigServletInner {

    @Autowired
    private LongPullingService longPullingService;

    /**
     * 轮询接口
     */
    public String doPollingConfig(HttpServletRequest request, HttpServletResponse response, Map<String, String> clientMd5Map, int probeRequestSize) throws IOException, ServletException {

        // 长轮询
        if (LongPullingService.isSupportLongPulling(request)) {
            longPullingService.addLongPullingClient(request, response, clientMd5Map, probeRequestSize);
            return HttpServletResponse.SC_OK + "";
        }

        // else 兼容短轮询逻辑
        List<String> changedGroups = ConfigController.compareMd5(request, response, clientMd5Map);

        // 兼容短轮询result
        String oldResult = ConfigController.compareMd5OldResult(changedGroups);
        String newResult = ConfigController.compareMd5ResultString(changedGroups);

        String version = request.getHeader(Constants.CLIENT_VERSION_HEADER);
        if (version == null) {
            version = "2.0.0";
        }
        int versionNum = Protocol.getVersionNumber(version);

        if (versionNum < 204) { // 2.0.4版本以前, 返回值放入header中
            response.addHeader(Constants.PROBE_MODIFY_RESPONSE, oldResult);
            response.addHeader(Constants.PROBE_MODIFY_RESPONSE_NEW, newResult);
        } else {
            request.setAttribute("content", newResult);
        }

        // 禁用缓存
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-cache,no-store");

        ConfigServlet.forwardErrorPage(request, response, HttpServletResponse.SC_OK);
        return HttpServletResponse.SC_OK + "";
    }

    /**
     * 同步配置获取接口
     */
    public String doGetConfig(HttpServletRequest request, HttpServletResponse response, String dataId, String group, String clientIp)
            throws IOException, ServletException {
        final String groupKey = GroupKey2.getKey(dataId, group);

        int lockResult = tryConfigReadLock(request, response, groupKey);

        final String requestIp = RequestUtil.getRemoteIp(request);

        if (lockResult > 0) {
            FileInputStream fis = null;
            try {
                File file = DiskUtil.targetFile(dataId, group);
                fis = new FileInputStream(file);

                String md5 = ConfigService.getContentMd5(groupKey);
                long lastModified = ConfigService.getLastModifiedTs(groupKey);

                response.setHeader(Constants.CONTENT_MD5, md5);
                response.setHeader("Pragma", "no-cache"); // 禁用缓存
                response.setDateHeader("Expires", 0);
                response.setHeader("Cache-Control", "no-cache,no-store");
                response.setDateHeader("Last-Modified", file.lastModified());

                fis.getChannel().transferTo(0L, fis.getChannel().size(),
                        Channels.newChannel(response.getOutputStream()));

                LogUtil.pullCheckLog.warn("{}|{}|{}|{}", new Object[]{groupKey,requestIp,md5, TimeUtil.getCurrentTime()});

                final long delayed = System.currentTimeMillis() - lastModified;

                // TODO distinguish pull-get && push-get 否则无法直接把delayed作为推送延时的依据，因为主动get请求的delayed值都很大
                ConfigTraceService.logPullEvent(dataId, group, lastModified, ConfigTraceService.PULL_EVENT_OK, delayed, requestIp);

            } finally {
                releaseConfigReadLock(groupKey);
                if (null != fis) fis.close();
            }
        } else if (lockResult == 0) {

            // FIXME CacheItem 不存在了无法简单的计算推送delayed，这里简单的记做-1
            ConfigTraceService.logPullEvent(dataId, group, -1, ConfigTraceService.PULL_EVENT_NOTFOUND, -1, requestIp);

            //pullLog.info("[client-get] clientIp={}, {}, no data", new Object[]{clientIp, groupKey});

            ConfigServlet.forwardErrorPage(request, response, HttpServletResponse.SC_NOT_FOUND);
            return HttpServletResponse.SC_NOT_FOUND + "";

        } else {
            // ConfigTraceService.logPullEvent(dataId, group, -1, ConfigTraceService.PULL_EVENT_CONFLICT, -1, requestIp);

            LogUtil.pullLog.info("[client-get] clientIp={}, {}, get data during dump", new Object[]{clientIp, groupKey});

            ConfigServlet.forwardErrorPage(request, response, HttpServletResponse.SC_CONFLICT);
            return HttpServletResponse.SC_CONFLICT + "";

        }

        return HttpServletResponse.SC_OK + "";
    }

    /**
     * 同步配置批量获取接口
     */
    public String doBatchGetConfig(HttpServletRequest request, HttpServletResponse response, String dataIds, String group, ModelMap modelMap)
            throws IOException, ServletException {
        final String clientIp = RequestUtil.getRemoteIp(request);

        List<ConfigInfoEx> configs = new ArrayList<ConfigInfoEx>();
        boolean failed = false;

        String[] dataIdArray = dataIds.split(Constants.WORD_SEPARATOR);
        for (String dataId : dataIdArray) {
            final String groupKey = GroupKey2.getKey(dataId, group);

            ConfigInfoEx config = null;

            int lockResult = tryConfigReadLock(request, response, groupKey);
            if (lockResult > 0) { // get config snapshot
                FileInputStream fis = null;
                try {
                    File file = DiskUtil.targetFile(dataId, group);
                    fis = new FileInputStream(file);
                    String content = IOUtils.toString(fis, Constants.ENCODE);
                    config = new ConfigInfoEx(dataId, group, content, Constants.BATCH_QUERY_EXISTS, Constants.BATCH_QUERY_EXISTS_MSG);
                } catch (IOException e) {
                    failed = true;
                    config = new ConfigInfoEx(dataId, group, null, Constants.BATCH_OP_ERROR, Constants.BATCH_OP_ERROR_IO_MSG);
                    LogUtil.pullLog.error("[batch-get-error] clientIp={}, {}, md5={}", new Object[]{clientIp, groupKey, ConfigService.getContentMd5(groupKey)}, e);
                } finally {
                    releaseConfigReadLock(groupKey);
                    if (null != fis) fis.close();
                }
            } else if (lockResult == 0) { // no config
                config = new ConfigInfoEx(dataId, group, null, Constants.BATCH_QUERY_NONEXISTS, Constants.BATCH_QUERY_NONEEXISTS_MSG);
                LogUtil.pullLog.info("[batch-get-nodata] clientIp={}, {}, md5={}", new Object[]{clientIp, groupKey, ConfigService.getContentMd5(groupKey)});
            } else { // conflict
                failed = true;
                config = new ConfigInfoEx(dataId, group, null, Constants.BATCH_OP_ERROR, Constants.BATCH_OP_ERROR_CONFLICT_MSG);
                LogUtil.pullLog.error("[batch-get-conflict] clientIp={}, {}, md5={}", new Object[]{clientIp, groupKey, ConfigService.getContentMd5(groupKey)});
            }

            configs.add(config);
        }

        try {
            String json = JSONUtils.serializeObject(configs);
            modelMap.addAttribute("json", json);
        } catch (Exception e) {
            LogUtil.pullLog.error("[batch-get-config-error] serialize result error, clientIp={}, group={}, dataIds={}", new Object[]{clientIp, group, dataIds}, e);
            throw new RuntimeException("[batch-get-config-error] serialize result error", e);
        }

        if (failed) {
            // 只提供全部成功的批量查询语义，部分失败返回http code 412
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            return HttpServletResponse.SC_PRECONDITION_FAILED + "";
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            return HttpServletResponse.SC_OK + "";
        }
    }


    private static void releaseConfigReadLock(String groupKey) {
        ConfigService.releaseReadLock(groupKey);
    }

    private static int tryConfigReadLock(HttpServletRequest request, HttpServletResponse response, String groupKey) throws IOException, ServletException {
        int lockResult = -1; // 默认加锁失败
        // 尝试加锁，最多10次
        for (int i = 9; i >= 0; --i) {
            lockResult = ConfigService.tryReadLock(groupKey);

            if (0 == lockResult) { // 数据不存在
                break;
            }

            if (lockResult > 0) { // success
                break;
            }

            if (i > 0) { // retry
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                }
            }
        }

        return lockResult;
    }


}
