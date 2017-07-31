package com.le.diamond.server.controller;

import com.le.diamond.common.Constants;
import com.le.diamond.domain.ConfigInfo;
import com.le.diamond.domain.ConfigInfoEx;
import com.le.diamond.domain.Page;
import com.le.diamond.domain.SubscriberStatus;
import com.le.diamond.server.DiamondServiceException;
import com.le.diamond.server.service.*;
import com.le.diamond.server.service.trace.ConfigTraceService;
import com.le.diamond.server.utils.GroupKey2;
import com.le.diamond.server.utils.RequestUtil;
import com.le.diamond.server.utils.SystemConfig;
import com.le.diamond.utils.ContentUtils;
import com.le.diamond.utils.JSONUtils;
import com.le.diamond.utils.ParamUtils;
import com.le.diamond.utils.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 管理控制器。 
 *
 * sdk
 * 管理后台
 *
 * 全都是非聚合的。
 */
@Controller
@RequestMapping("/admin.do")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    protected LongPullingService longPullingService;

    @Autowired
    protected AdminService adminService;

    @Autowired
    protected PersistService persistService;

    @Autowired
    DatumController datumController;

    @Autowired
    BaseStoneController basestoneController;

    private static boolean isSDKRequest(HttpServletRequest req) {
        String accept = req.getHeader("Accept");
        if (accept != null && accept.indexOf("application/json") >= 0) {
            return true;
        }
        return false;
    }

    public static String getRemoteIp(HttpServletRequest req) {
        String remoteIp = req.getHeader("X-Real-IP");
        if (remoteIp == null || remoteIp.isEmpty()) {
            remoteIp = req.getRemoteAddr();
        }
        return remoteIp;
    }

    @RequestMapping(params = "method=isClientInPolling", method = RequestMethod.GET)
    public String isClientInPolling(HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam("clientIp")String clientIp, ModelMap modelMap) {

        if(StringUtils.isBlank(clientIp)) {
            modelMap.addAttribute("content", clientIp + " is empty");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return HttpServletResponse.SC_BAD_REQUEST + "";
        }

        try {
            URL testUrl=new URL("http://"+clientIp);
        } catch (Exception e) {
            modelMap.addAttribute("content", clientIp + " is invalid");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return HttpServletResponse.SC_BAD_REQUEST + "";
        }

        String result = String.valueOf(longPullingService.isClientLongPolling(clientIp));
        modelMap.addAttribute("content", result);
        response.setStatus(HttpServletResponse.SC_OK);
        return HttpServletResponse.SC_OK + "";
    }

    @RequestMapping(params = "method=listClientSubConfig", method = RequestMethod.GET)
    public String listClientSubConfig(HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam("clientIp")String clientIp, ModelMap modelMap) throws Exception {

        if(StringUtils.isBlank(clientIp)) {
            modelMap.addAttribute("content", clientIp + " is empty");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return HttpServletResponse.SC_BAD_REQUEST + "";
        }

        try {
            URL testUrl=new URL("http://"+clientIp);
        } catch (Exception e) {
            modelMap.addAttribute("content", clientIp + " is invalid");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return HttpServletResponse.SC_BAD_REQUEST + "";
        }


        Map<String, String> clientSubConfigInfo = longPullingService.getClientSubConfigInfo(clientIp);
        String str = JSONUtils.serializeObject(clientSubConfigInfo);
        modelMap.addAttribute("content", str);

        response.setStatus(HttpServletResponse.SC_OK);
        return HttpServletResponse.SC_OK + "";

    }

    @RequestMapping(params = "method=listAllClientSubConfig", method = RequestMethod.GET)
    public String listAllClientSubConfig(HttpServletRequest request, HttpServletResponse response,
                                         @RequestParam("dataId") String dataId, @RequestParam("group") String group, ModelMap modelMap) throws Exception {

        List<Map<String, String>> allClientSubConfig = longPullingService.getAllClientSubConfig(dataId, group);
        modelMap.addAttribute("dataId", dataId);
        modelMap.addAttribute("group", group);
        modelMap.addAttribute("clientInfo", allClientSubConfig);
        return "/admin/config/client";
    }

    /**
     * 增加更新非聚合配置信息。
     */
    @RequestMapping(params = "method=postConfigNew", method = RequestMethod.POST)
    public String updateConfig3(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam("dataId")
                                String dataId, @RequestParam("group")
                                String group, @RequestParam("content")
                                String content, @RequestParam(value = "src_user", required = false)
                                String srcUser, ModelMap modelMap) {
        return postConfig(request, response, dataId, group, content, srcUser, modelMap);
    }

    /**
     * 增加更新非聚合配置信息。
     */
    @RequestMapping(params = "method=updateConfigNew", method = RequestMethod.POST)
    public String updateConfig2(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam("dataId")
                                String dataId, @RequestParam("group")
                                String group, @RequestParam("content")
                                String content, @RequestParam(value = "src_user", required = false)
                                String srcUser, ModelMap modelMap) {
        return postConfig(request, response, dataId, group, content, srcUser, modelMap);
    }

    /**
     * 增加更新非聚合配置信息。
     */
    @RequestMapping(params = "method=updateConfig", method = RequestMethod.POST)
    public String updateConfig(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam("dataId")
                               String dataId, @RequestParam("group")
                               String group, @RequestParam("content")
                               String content, @RequestParam(value = "src_user", required = false)
                               String srcUser, ModelMap modelMap) {
        return postConfig(request, response, dataId, group, content, srcUser, modelMap);
    }

    /**
     * 增加更新非聚合配置信息.
     */
    @RequestMapping(params = "method=postConfig", method = RequestMethod.POST)
    public String postConfig(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam("dataId")
                             String dataId, @RequestParam("group")
                             String group, @RequestParam("content")
                             String content, @RequestParam(value = "src_user", required = false)
                             String srcUser, ModelMap modelMap) {
        final String clientIp = RequestUtil.getRemoteIp(request);
        dataId = dataId.trim();
        group = group.trim();

//         sdk没有传src_user，等sdk废除后再验证user
//        if (StringUtils.isBlank(srcUser)) {
//            log.warn("[admin.do] post config but src_user not specified. clientIp={}, {}, {}",
//                    new Object[] { clientIp, dataId, group });
//            
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            modelMap.addAttribute("content", "param src_user not specified");
//            return HttpServletResponse.SC_FORBIDDEN + "";
//        }

        log.info(
                "[admin.do] post config. clientIp={}, user={}, dataId={} group={}, newConfig={}",
                new Object[] { clientIp, srcUser, dataId, group,
                        ContentUtils.truncateContent(content) });

        String resultCode = basestoneController.syncUpdateConfigAll(request, response, dataId,
                group, content, modelMap);

        // 区分SDK请求和来自管理后台的请求
        if (isSDKRequest(request)) {
            return resultCode;
        } else {
            if (String.valueOf(HttpServletResponse.SC_OK).equals(resultCode)) {
                modelMap.addAttribute("message", "replace success!");
                return listConfig(request, response, dataId, group, 1, 20, modelMap);
            } else {
                modelMap.addAttribute("message", "delete fail! " + resultCode);
                return "/admin/config/list";
            }
        }
    }


    /**
     * 根据数据库主键ID删除配置信息。  有用到。 TODO 记录调用端，推动使用根据dataId和groupId来删除。
     */
    @RequestMapping(params = "method=deleteConfig", method = RequestMethod.GET)
    public String deleteConfig(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam("id")
                               long id, ModelMap modelMap) {

        // 根据id查询出该条数据
        ConfigInfo configInfo = persistService.findConfigInfo(id);
        if (configInfo == null) {
            if (isSDKRequest(request)) {
                response.setStatus(HttpServletResponse.SC_OK);
                return String.valueOf(HttpServletResponse.SC_OK);
            } else {
                modelMap.addAttribute("message", "not found");
                return "/admin/config/list";
            }
        }

        String dataId = configInfo.getDataId();
        String group = configInfo.getGroup();
        return deleteConfigByDataIdGroup(request, response, dataId, group, modelMap);
    }

    /**
     * 根据dataId和group删除配置信息
     */
    @RequestMapping(params = "method=deleteConfigByDataIdGroup", method = RequestMethod.GET)
    public String deleteConfigByDataIdGroup(HttpServletRequest request,
                                            HttpServletResponse response, @RequestParam("dataId")
    String dataId, @RequestParam("group")
                                            String group, ModelMap modelMap) {

        String resultCode = datumController.deleteAllDatum(request, response, dataId, group,
                modelMap);

        // 区分SDK请求和来自管理后台的请求
        if (isSDKRequest(request)) {
            return resultCode;
        } else {
            if (String.valueOf(HttpServletResponse.SC_OK).equals(resultCode)) {
                modelMap.addAttribute("message", "delete success!");
                return "/admin/config/list";
            } else {
                modelMap.addAttribute("message", "delete fail! " + resultCode);
                return "/admin/config/list";
            }
        }
    }


    /**
     * 查询配置信息，返回JSON格式。
     */
    @RequestMapping(params = "method=listConfig", method = RequestMethod.GET)
    public String listConfig(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam("dataId") String dataId, @RequestParam("group") String group,
                             @RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize, ModelMap modelMap) {

        Page<ConfigInfo> page = findConfigInfo(pageNo, pageSize, group, dataId);

        if (isSDKRequest(request)) {
            try {
                String json = JSONUtils.serializeObject(page);
                modelMap.addAttribute("json", json);
                response.setStatus(HttpServletResponse.SC_OK);
                return "/admin/config/list_json";
            }
            catch (Exception e) {
                String errorMsg = "serialize page error, dataId=" + dataId + ", group=" + group;
                log.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
            }
        }
        else {
            modelMap.addAttribute("dataId", dataId);
            modelMap.addAttribute("group", group);
            modelMap.addAttribute("page", page);
            return "/admin/config/list";
        }
    }

    /**
     * 模糊查询配置信息。不允许只根据内容模糊查询，即dataId和group都为NULL，但content不是NULL。这种情况下，返回所有配置。
     */
    @RequestMapping(params = "method=listConfigLike", method = RequestMethod.GET)
    public String listConfigLike(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam("dataId")
                                 String dataId, //
                                 @RequestParam("group")
                                 String group, //
                                 @RequestParam(value = "content", required = false, defaultValue = "")
                                 String content, //
                                 @RequestParam("pageNo")
                                 int pageNo, //
                                 @RequestParam("pageSize")
                                 int pageSize, //
                                 ModelMap modelMap) {

        Page<ConfigInfo> page = persistService.findConfigInfoLike(pageNo, pageSize, dataId, group,
                content);

        if (isSDKRequest(request)) {
            try {
                String json = JSONUtils.serializeObject(page);
                modelMap.addAttribute("json", json);
                response.setStatus(HttpServletResponse.SC_OK);
                return "/admin/config/list_json";
            } catch (Exception e) {
                String errorMsg = "serialize page error, dataId=" + dataId + ", group=" + group;
                log.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
            }
        } else {
            modelMap.addAttribute("page", page);
            modelMap.addAttribute("dataId", dataId);
            modelMap.addAttribute("group", group);
            modelMap.addAttribute("method", "listConfigLike");
            return "/admin/config/list";
        }
    }


    /**
     * 查看配置信息详情
     */
    @RequestMapping(params = "method=detailConfig", method = RequestMethod.GET)
    public String getConfigInfo(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam("dataId") String dataId, @RequestParam("group") String group, ModelMap modelMap) {

        ConfigInfo configInfo = persistService.findConfigInfo(dataId, group);
        modelMap.addAttribute("configInfo", configInfo);
        return "/admin/config/edit";
    }


    /**
     * 展示所有用户
     *
     * @param modelMap
     * @return
     */
    @RequestMapping(params = "method=listUser", method = RequestMethod.GET)
    public String listUser(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        Map<String, String> userMap = this.adminService.getAllUsers();
        modelMap.addAttribute("userMap", userMap);
        return "/admin/user/list";
    }


    /**
     * 添加用户
     *
     * @param modelMap
     * @return
     */
    @RequestMapping(params = "method=addUser", method = RequestMethod.POST)
    public String addUser(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam("userName") String userName, @RequestParam("password") String password, ModelMap modelMap) {
        if (StringUtils.isBlank(userName) || !ParamUtils.isValid(userName.trim())) {
            modelMap.addAttribute("message", "invalid username");
            return listUser(request, response, modelMap);
        }
        if (StringUtils.isBlank(password) || !ParamUtils.isValid(password.trim())) {
            modelMap.addAttribute("message", "invalid password");
            return listUser(request, response, modelMap);
        }

        if (this.adminService.addUser(userName, password)) {
            modelMap.addAttribute("message", "add success!");
        }
        else {
            modelMap.addAttribute("message", "add fail!");
        }

        return listUser(request, response, modelMap);
    }


    /**
     * 删除用户
     *
     * @param modelMap
     * @return
     */
    @RequestMapping(params = "method=deleteUser", method = RequestMethod.GET)
    public String deleteUser(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam("userName") String userName, ModelMap modelMap) {
        if (StringUtils.isBlank(userName) || !ParamUtils.isValid(userName.trim())) {
            modelMap.addAttribute("message", "invalid username");
            return listUser(request, response, modelMap);
        }

        if (this.adminService.removeUser(userName)) {
            modelMap.addAttribute("message", "delete success!");
        }
        else {
            modelMap.addAttribute("message", "delete fail!");
        }

        return listUser(request, response, modelMap);
    }


    /**
     * 更改密码
     *
     * @param userName
     * @param password
     * @param modelMap
     * @return
     */
    @RequestMapping(params = "method=changePassword", method = RequestMethod.GET)
    public String changePassword(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam("userName") String userName, @RequestParam("password") String password, ModelMap modelMap) {

        if (StringUtils.isBlank(userName) || !ParamUtils.isValid(userName.trim())) {
            modelMap.addAttribute("message", "invalid username");
            return listUser(request, response, modelMap);
        }
        if (StringUtils.isBlank(password) || !ParamUtils.isValid(password.trim())) {
            modelMap.addAttribute("message", "invalid password");
            return listUser(request, response, modelMap);
        }

        if (this.adminService.updatePassword(userName, password)) {
            modelMap.addAttribute("message", "update success!");
        }
        else {
            modelMap.addAttribute("message", "update fail!");
        }

        return listUser(request, response, modelMap);
    }

    /**
     * 重新文件加载用户信息
     *
     * @param modelMap
     * @return
     */
    @RequestMapping(params = "method=reloadUser", method = RequestMethod.GET)
    public String reloadUser(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        this.adminService.loadUsers();
        modelMap.addAttribute("message", "reload success!");
        return listUser(request, response, modelMap);
    }


    // =========================== 批量处理 ============================== //

    // TODO 是否被sdk使用？？
    @RequestMapping(params = "method=batchQuery", method = RequestMethod.POST)
    public String batchQuery(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam("dataIds") String dataIds, @RequestParam("group") String group, ModelMap modelMap) {

        // 这里抛出的异常, 会产生一个500错误, 返回给sdk, sdk会将500错误记录到日志中
        if (StringUtils.isBlank(dataIds)) {
            throw new IllegalArgumentException("batch query, invalid dataIds");
        }
        // group对批量操作的每一条数据都相同, 不需要在for循环里面进行判断
        if (StringUtils.isBlank(group)) {
            throw new IllegalArgumentException("batch query, invalid group");
        }

        // 分解dataId
        String[] dataIdArray = dataIds.split(Constants.WORD_SEPARATOR);
        group = group.trim();

        List<ConfigInfoEx> configInfoExList = new ArrayList<ConfigInfoEx>();
        for (String dataId : dataIdArray) {
            ConfigInfoEx configInfoEx = new ConfigInfoEx();
            configInfoEx.setDataId(dataId);
            configInfoEx.setGroup(group);
            configInfoExList.add(configInfoEx);
            try {
                if (StringUtils.isBlank(dataId)) {
                    configInfoEx.setStatus(Constants.BATCH_QUERY_NONEXISTS);
                    configInfoEx.setMessage("dataId is blank");
                    continue;
                }

                // 查询数据库
                ConfigInfo configInfo = persistService.findConfigInfo(dataId, group);
                if (configInfo == null) {
                    // 没有异常, 说明查询成功, 但数据不存在, 设置不存在的状态码
                    configInfoEx.setStatus(Constants.BATCH_QUERY_NONEXISTS);
                    configInfoEx.setMessage("query data does not exist");
                }
                else {
                    // 没有异常, 说明查询成功, 而且数据存在, 设置存在的状态码
                    String content = configInfo.getContent();
                    configInfoEx.setContent(content);
                    configInfoEx.setStatus(Constants.BATCH_QUERY_EXISTS);
                    configInfoEx.setMessage("query success");
                }
            }
            catch (Exception e) {
                log.error("batch query error, dataId=" + dataId + ",group=" + group, e);
                // 出现异常, 设置异常状态码
                configInfoEx.setStatus(Constants.BATCH_OP_ERROR);
                configInfoEx.setMessage("query error: " + e.getMessage());
            }
        }

        String json = null;
        try {
            json = JSONUtils.serializeObject(configInfoExList);
            modelMap.addAttribute("json", json);
            response.setStatus(HttpServletResponse.SC_OK);
            return "/admin/config/batch_result";
        }
        catch (Exception e) {
            log.error("batch query serialize result error, group=" + group + ", dataIds=\n" + dataIds, e);
            throw new RuntimeException("batch query serialize result error", e);
        }
    }

    // TODO 是否被sdk使用？？
    @RequestMapping(params = "method=batchAddOrUpdate", method = RequestMethod.POST)
    public String batchAddOrUpdate(HttpServletRequest request, HttpServletResponse response,
                                   @RequestParam("allDataIdAndContent") String allDataIdAndContent, @RequestParam("group") String group,
                                   @RequestParam("src_user") String srcUser, ModelMap modelMap) {

        // 这里抛出的异常, 会产生一个500错误, 返回给sdk, sdk会将500错误记录到日志中
        if (StringUtils.isBlank(allDataIdAndContent)) {
            throw new IllegalArgumentException("batch write, invalid allDataIdAndContent");
        }

        // group对批量操作的每一条数据都相同, 不需要在for循环里面进行判断
        if (StringUtils.isBlank(group) || !ParamUtils.isValid(group)) {
            throw new IllegalArgumentException("batch write, invalid group");
        }

        String remoteIp = getRemoteIp(request);

        String[] dataIdAndContentArray = allDataIdAndContent.split(Constants.LINE_SEPARATOR);

        List<ConfigInfoEx> configInfoExList = new ArrayList<ConfigInfoEx>();
        for (String dataIdAndContent : dataIdAndContentArray) {
            String dataId = dataIdAndContent.substring(0, dataIdAndContent.indexOf(Constants.WORD_SEPARATOR));
            String content = dataIdAndContent.substring(dataIdAndContent.indexOf(Constants.WORD_SEPARATOR) + 1);
            ConfigInfoEx configInfoEx = new ConfigInfoEx();
            configInfoEx.setDataId(dataId);
            configInfoEx.setGroup(group);
            configInfoEx.setContent(content);

            try {
                // 判断dataId是否包含非法字符
                if (StringUtils.isBlank(dataId) || !ParamUtils.isValid(dataId)) {
                    // 这里抛出的异常, 会在下面catch, 然后设置状态, 保证一个dataId的异常不会影响其他dataId
                    throw new IllegalArgumentException("batch write, invalid dataId");
                }
                // 判断内容是否为空
                if (StringUtils.isBlank(content)) {
                    throw new IllegalArgumentException("batch write, invalid content");
                }

                // 查询数据库
                ConfigInfo configInfo = persistService.findConfigInfo(dataId, group);

                final Timestamp time = TimeUtils.getCurrentTime();
                if (configInfo == null) {
                    // 数据不存在, 新增
                    persistService.addConfigInfo(dataId, group, content, remoteIp, srcUser, time);

                    // 新增成功, 设置状态码
                    configInfoEx.setStatus(Constants.BATCH_ADD_SUCCESS);
                    configInfoEx.setMessage("add success");

                    ConfigTraceService.logPersistenceEvent(dataId, group, time.getTime(), SystemConfig.LOCAL_IP, ConfigTraceService.PERSISTENCE_EVENT_PUB, content);
                }
                else {
                    // 数据存在, 更新
                    persistService.updateConfigInfo(dataId, group, content, remoteIp, srcUser, time);
                    // 更新成功, 设置状态码
                    configInfoEx.setStatus(Constants.BATCH_UPDATE_SUCCESS);
                    configInfoEx.setMessage("update success");

                    ConfigTraceService.logPersistenceEvent(dataId, group, time.getTime(), SystemConfig.LOCAL_IP, ConfigTraceService.PERSISTENCE_EVENT_PUB, content);
                }
            }
            catch (Exception e) {
                log.error("batch write error, dataId=" + dataId + ",group=" + group + ",config=" + ContentUtils.truncateContent(content), e);
                // 出现异常, 设置异常状态码
                configInfoEx.setStatus(Constants.BATCH_OP_ERROR);
                configInfoEx.setMessage("batch write error: " + e.getMessage());
            }
            configInfoExList.add(configInfoEx);
        }

        String json = null;
        try {
            json = JSONUtils.serializeObject(configInfoExList);
            modelMap.addAttribute("json", json);
            response.setStatus(HttpServletResponse.SC_OK);
            return "/admin/config/batch_result";
        }
        catch (Exception e) {
            log.error("batch query serialize result error, group=" + group + ", allDataIdAndContent=\n"
                    + allDataIdAndContent, e);
            throw new RuntimeException("batch write serialize result error", e);
        }
    }

    /**
     * 重新加载聚合dataId白名单。
     */
    @RequestMapping(params = "method=getAggrWhitelist", method = RequestMethod.GET)
    public String loadAggrWhitelist(HttpServletRequest request, HttpServletResponse response,
                                    ModelMap modelMap) throws IOException {
        modelMap.addAttribute("content", AggrWhitelist.getWhiteList());
        response.setStatus(HttpServletResponse.SC_OK);
        return HttpServletResponse.SC_OK + "";
    }





    /**
     * 查询客户端所有订阅数据是否是最新的
     */
    @RequestMapping(params = "method=listSubscriber", method = RequestMethod.GET)
    public String listSubscriber(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam("clientIp")
                                 String clientIp, ModelMap modelMap) throws Exception {
        Map<String,Boolean> result = ClientTrackService.isClientUptodate(clientIp);
        String str = JSONUtils.serializeObject(result);
        modelMap.addAttribute("content", str);

        response.setStatus(HttpServletResponse.SC_OK);
        return HttpServletResponse.SC_OK + "";
    }

    /**
     * 查询指定数据的所有订阅者
     */
    @RequestMapping(params = "method=listSubscriberByGroup", method = RequestMethod.GET)
    public String listSubscriberByGroup(HttpServletRequest request, HttpServletResponse response,
                                        @RequestParam("dataId")
                                        String dataId, @RequestParam("group")
                                        String group, ModelMap modelMap) throws Exception {
        String groupKey = GroupKey2.getKey(dataId, group);
        Map<String,Boolean> subs = ClientTrackService.listSubscriberByGroup(groupKey);
        String str = JSONUtils.serializeObject(subs);
        modelMap.addAttribute("content", str);

        response.setStatus(HttpServletResponse.SC_OK);
        return HttpServletResponse.SC_OK + "";
    }

    /**
     * 根据clientip返回server端保存的客户端最近一次轮询时所有配置的md5
     * groupkey -> SubscriberStatus
     */
    @RequestMapping(params = "method=listSubStatus", method = RequestMethod.GET)
    public String listSubStatus(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam("clientIp")
                                 String clientIp, ModelMap modelMap) throws Exception {
        Map<String,SubscriberStatus> result = ClientTrackService.listSubStatus(clientIp);
        String str = JSONUtils.serializeObject(result);
        modelMap.addAttribute("content", str);

        response.setStatus(HttpServletResponse.SC_OK);
        return HttpServletResponse.SC_OK + "";
    }

    /**
     * 根据配置的dataid， group 查询本机上所有订阅者
     * clientIp -> SubscriberStatus
     */
    @RequestMapping(params = "method=listSubsByGroup", method = RequestMethod.GET)
    public String listSubsByGroup(HttpServletRequest request, HttpServletResponse response,
                                        @RequestParam("dataId")
                                        String dataId, @RequestParam("group")
                                        String group, ModelMap modelMap) throws Exception {
        String groupKey = GroupKey2.getKey(dataId, group);
        Map<String,SubscriberStatus> subs = ClientTrackService.listSubsByGroup(groupKey);
        String str = JSONUtils.serializeObject(subs);
        modelMap.addAttribute("content", str);

        response.setStatus(HttpServletResponse.SC_OK);
        return HttpServletResponse.SC_OK + "";
    }


    /**
     * 分页查找配置信息
     */
    public Page<ConfigInfo> findConfigInfo(final int pageNo, final int pageSize,
                                           final String group, final String dataId) {
        try {
            if (!StringUtils.isBlank(dataId) && !StringUtils.isBlank(group)) {
                ConfigInfo configInfo = this.persistService.findConfigInfo(dataId, group);
                Page<ConfigInfo> page = new Page<ConfigInfo>();
                if (configInfo != null) {
                    page.setPageNumber(1);
                    page.setTotalCount(1);
                    page.setPagesAvailable(1);
                    page.getPageItems().add(configInfo);
                }
                return page;
            } else if (!StringUtils.isBlank(dataId)) {
                return this.persistService.findConfigInfoByDataId(pageNo, pageSize, dataId);
            } else if (!StringUtils.isBlank(group)) {
                return this.persistService.findConfigInfoByGroup(pageNo, pageSize, group);
            } else {
                return this.persistService.findAllConfigInfo(pageNo, pageSize);
            }
        } catch (Exception e) {
            String errorMsg = "query page config info error, dataId=" + dataId + ", group=" + group
                    + ", pageNo=" + pageNo + ", pageSize=" + pageSize;
            log.error(errorMsg, e);
            throw new DiamondServiceException(e);
        }
    }


}
