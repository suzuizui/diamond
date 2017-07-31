package com.le.diamond.server.aspect;

import com.le.diamond.md5.MD5;
import com.le.diamond.server.service.ConfigService;
import com.le.diamond.server.utils.GroupKey2;
import com.le.diamond.server.utils.LogUtil;
import com.le.diamond.server.utils.RequestUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: dingjoey
 * Date: 13-12-12
 * Time: 21:12
 * client api && sdk api 请求日志打点逻辑
 */
@Aspect
public class RequestLogAspect {
    // ----------------------------------
    // client api

    // get config
    public static final String CLIENT_INTERFACE_GET_CONFIG = "execution(* com.le.diamond.server.controller.ConfigServletInner.doGetConfig(..)) && args(request,response,dataId,group,clientIp,..)";
    public static final String CLIENT_INTERFACE_BATCH_GET_CONFIG = "execution(* com.le.diamond.server.controller.ConfigServletInner.doBatchGetConfig(..)) && args(request,response,dataIds,group,..)";

    // publish single aggr
    public static final String CLIENT_INTERFACE_PUBLISH_SINGLE_CONFIG = "execution(* com.le.diamond.server.controller.BaseStoneController.syncUpdateConfigAll(..)) && args(request,response,dataId,group,content,..)";
    public static final String CLIENT_INTERFACE_PUBLISH_AGGR_CONFIG = "execution(* com.le.diamond.server.controller.DatumController.addDatum(..)) && args(request,response,dataId,group,datumId,content,..)";
    // remove all  single
    public static final String CLIENT_INTERFACE_REMOVE_ALL_CONFIG = "execution(* com.le.diamond.server.controller.DatumController.deleteAllDatum(..)) && args(request,response,dataId,group,..)";
    public static final String CLIENT_INTERFACE_REMOVE_AGGR_CONFIG = "execution(* com.le.diamond.server.controller.DatumController.deleteSingleDatum(..)) && args(request,response,dataId,group,datumId,..)";

    // ----------------------------------
    // sdk api

    // sdk list listlike config
    public static final String SDK_INTERFACE_LSIT_CONFIG = "execution(* com.le.diamond.server.controller.AdminController.listConfig(..)) && args(request,response,dataId,group,..)";
    public static final String SDK_INTERFACE_LSIT_LIKE_CONFIG = "execution(* com.le.diamond.server.controller.AdminController.listConfigLike(..)) && args(request,response,dataId,group,content,..)";

    // sdk remove all
    public static final String SDK_INTERFACE_REMOVE_CONFIG = "execution(* com.le.diamond.server.controller.AdminController.deleteConfigByDataIdGroup(..)) && args(request,response,dataId,group,..)";

    // sdk publish
    public static final String SDK_INTERFACE_PUBLISH_CONFIG = "execution(* com.le.diamond.server.controller.AdminController.postConfig(..)) && args(request,response,dataId,group,content,..)";

    // ----------------------------------
    // server
    //public static final String SERVER_NOTIFY = "execution(* NotifyController.notifyConfigInfo(..))";


    // ----------------------------------

    /**
     * remove aggr single
     */
    @Around(CLIENT_INTERFACE_REMOVE_AGGR_CONFIG)
    public Object interfaceRemoveAggr(ProceedingJoinPoint pjp, HttpServletRequest request, HttpServletResponse response, String dataId, String group, String datumId) throws Throwable {
        return logClientRequest("remove-aggr", pjp, request, response, dataId, group, datumId, null);
    }

    /**
     * removeAll
     */
    @Around(CLIENT_INTERFACE_REMOVE_ALL_CONFIG)
    public Object interfaceRemoveAll(ProceedingJoinPoint pjp, HttpServletRequest request, HttpServletResponse response, String dataId, String group) throws Throwable {
        return logClientRequest("remove", pjp, request, response, dataId, group, null, null);
    }

    /**
     * publishAggr
     */
    @Around(CLIENT_INTERFACE_PUBLISH_AGGR_CONFIG)
    public Object interfacePublishAggr(ProceedingJoinPoint pjp, HttpServletRequest request, HttpServletResponse response, String dataId, String group, String datumId, String content) throws Throwable {
        final String md5 = content == null ? null : MD5.getInstance().getMD5String(content);
        return logClientRequest("publish-aggr", pjp, request, response, dataId, group, datumId, md5);
    }

    /**
     * publishSingle
     */
    @Around(CLIENT_INTERFACE_PUBLISH_SINGLE_CONFIG)
    public Object interfacePublishSingle(ProceedingJoinPoint pjp, HttpServletRequest request, HttpServletResponse response, String dataId, String group, String content) throws Throwable {
        final String md5 = content == null ? null : MD5.getInstance().getMD5String(content);
        return logClientRequest("publish", pjp, request, response, dataId, group, null, md5);
    }

    /**
     * getConfig
     */
    @Around(CLIENT_INTERFACE_GET_CONFIG)
    public Object interfaceGetConfig(ProceedingJoinPoint pjp, HttpServletRequest request, HttpServletResponse response, String dataId, String group, String clientIp) throws Throwable {
        final String groupKey = GroupKey2.getKey(dataId, group);
        final String md5 = ConfigService.getContentMd5(groupKey);
        return logClientRequest("get", pjp, request, response, dataId, group, null, md5);
    }

    /**
     * batchGetConfig
     */
    @Around(CLIENT_INTERFACE_BATCH_GET_CONFIG)
    public Object interfaceBatchGetConfig(ProceedingJoinPoint pjp, HttpServletRequest request, HttpServletResponse response, String dataIds, String group) throws Throwable {
        return logClientRequest("batch-get", pjp, request, response, dataIds, group, null, null);
    }

    /**
     * client api request log
     * rt | status | requestIp | opType | dataId | group | datumId | md5
     */
    private Object logClientRequest(String requestType, ProceedingJoinPoint pjp, HttpServletRequest request, HttpServletResponse response, String dataId, String group, String datumId, String md5) throws Throwable {
        final String requestIp = RequestUtil.getRemoteIp(request);

        // rt
        final long st = System.currentTimeMillis();
        Object retVal = pjp.proceed();
        final long rt = System.currentTimeMillis() - st;

        // rt | status | requestIp | opType | dataId | group | datumId | md5
        LogUtil.clientLog.info("{}|{}|{}|{}|{}|{}|{}|{}", new Object[]{rt, retVal, requestIp, requestType, dataId, group, datumId, md5});
        return retVal;
    }

    // ----------------------------------

    /**
     * sdk list config
     */
    @Around(SDK_INTERFACE_LSIT_CONFIG)
    public Object interfaceListConfig(ProceedingJoinPoint pjp, HttpServletRequest request, HttpServletResponse response, String dataId, String group) throws Throwable {
        return logSDKRequest("list", pjp, request, response, dataId, group, null, null);
    }

    /**
     * sdk list like config
     */
    @Around(SDK_INTERFACE_LSIT_LIKE_CONFIG)
    public Object interfaceListLikeConfig(ProceedingJoinPoint pjp, HttpServletRequest request, HttpServletResponse response, String dataId, String group, String content) throws Throwable {
        return logSDKRequest("list-like", pjp, request, response, dataId, group, content, null);
    }

    /**
     * sdk publish config
     */
    @Around(SDK_INTERFACE_PUBLISH_CONFIG)
    public Object interfacePublishConfig(ProceedingJoinPoint pjp, HttpServletRequest request, HttpServletResponse response, String dataId, String group, String content) throws Throwable {
        final String md5 = MD5.getInstance().getMD5String(content);
        return logSDKRequest("sdk-pub", pjp, request, response, dataId, group, null, md5);
    }

    /**
     * sdk remove config
     */
    @Around(SDK_INTERFACE_REMOVE_CONFIG)
    public Object interfaceRemoveConfig(ProceedingJoinPoint pjp, HttpServletRequest request, HttpServletResponse response, String dataId, String group) throws Throwable {
        return logSDKRequest("sdk-remove", pjp, request, response, dataId, group, null, null);
    }

    /**
     * sdk api request log
     * rt | status | requestIp | opType | dataIdPattern | groupPattern | contentPattern | [md5]
     */
    private Object logSDKRequest(String requestType, ProceedingJoinPoint pjp, HttpServletRequest request, HttpServletResponse response, String dataIdPattern, String groupPattern, String contentPattern, String md5) throws Throwable {
        final String requestIp = RequestUtil.getRemoteIp(request);

        // rt
        final long st = System.currentTimeMillis();
        Object retVal = pjp.proceed();
        final long rt = System.currentTimeMillis() - st;

        // rt | status | requestIp | opType | dataIdPattern | groupPattern | contentPattern | [md5]
        LogUtil.sdkLog.info("{}|{}|{}|{}|{}|{}|{}|{}", new Object[]{rt, retVal, requestIp, requestType, dataIdPattern, groupPattern, contentPattern, md5});
        return retVal;
    }


    // ----------------------------------
    //@Around(SERVER_NOTIFY)
    /*
    public Object serverNotify(ProceedingJoinPoint pjp) throws Throwable {
        // String selfIp = SystemConfig.LOCAL_IP;

        // rt
        final long st = System.currentTimeMillis();
        Object retVal = pjp.proceed();
        final long rt = System.currentTimeMillis() - st;

        // rt | opType | ext(selfIp)
        // LogUtil.clientLog.info("{}|{}", new Object[]{rt, "notify"});
        return retVal;
    }
    */

}
