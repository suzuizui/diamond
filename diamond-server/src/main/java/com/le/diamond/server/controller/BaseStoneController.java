package com.le.diamond.server.controller;

import com.le.diamond.domain.ConfigInfo;
import com.le.diamond.server.service.AggrWhitelist;
import com.le.diamond.server.service.PersistService;
import com.le.diamond.server.service.trace.ConfigTraceService;
import com.le.diamond.server.utils.RequestUtil;
import com.le.diamond.server.utils.SystemConfig;
import com.le.diamond.utils.ContentUtils;
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
import java.sql.Timestamp;
import java.util.List;


/**
 * ���ؿͻ��˷�������ר�ÿ�����
 * 
 * @author leiwen
 * 
 */
@Controller
@RequestMapping("/basestone.do")
public class BaseStoneController {
    private static final Logger log = LoggerFactory.getLogger(BaseStoneController.class);
    
    @Autowired
    private DatumController datumController;
    
    @Autowired
    private PersistService persistService;
    
    /**
     * �����ۺ����ݡ�ԭ�еĿͻ����߼�: �������û�����ݣ�����ø÷����������в�����identity��
     * 
     * TODO �ϳ��÷�����
     */
    @RequestMapping(params = "method=postConfig", method = RequestMethod.POST)
    public String postConfig(HttpServletRequest request, HttpServletResponse response,
            ModelMap modelMap, @RequestParam("dataId")
            String dataId, @RequestParam("group")
            String group, @RequestParam("content")
            String content) {

        String datumId = null;
        
        if (dataId.startsWith("NS_DIAMOND_SUBSCRIPTION_TOPIC_")) {
            int idx = content.indexOf(' ');
            datumId = content.substring(0, idx);
        } else if (dataId.equals("com.le.software.center.server.registry")) {
            datumId = content;
        } else {
            datumId = "noDatumId";
        }
        
        return datumController.addDatum(request, response, dataId, group, datumId, content,
                modelMap);
    }


    /**
     * ���ӻ���µ���������Ŀ
     */
    @RequestMapping(params = "method=syncUpdateConfig", method = RequestMethod.POST)
    public String syncUpdateConfig(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("dataId")
            String dataId, @RequestParam("group")
            String group, @RequestParam("content")
            String content, ModelMap modelMap) {

        return asyncUpdateConfig(request, response, dataId, group, content, modelMap);
    }
    /**
     * ���ӻ���µ���������Ŀ
     */
    @RequestMapping(params = "method=updateConfig", method = RequestMethod.POST)
    public String asyncUpdateConfig(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("dataId")
            String dataId, @RequestParam("group")
            String group, @RequestParam("content")
            String content, ModelMap modelMap) {

        String datumId = ContentUtils.getContentIdentity(content);
        String realContent = ContentUtils.getContent(content);

        return datumController.addDatum(request, response, dataId, group, datumId, realContent,
                modelMap);
    }

    /**
     * ɾ������������Ŀ
     */
    @RequestMapping(params = "method=syncDeleteConfig", method = RequestMethod.POST)
    public String syncDeleteConfig(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("dataId")
            String dataId, @RequestParam("group")
            String group, @RequestParam("content")
            String content, ModelMap modelMap) {
        return asyncDeleteConfig(request, response, dataId, group, content, modelMap);
    }
    /**
     * ����datum�������ݣ�ɾ��һ�������������Ŀ��
     */
    @RequestMapping(params = "method=deleteConfig", method = RequestMethod.POST)
    public String asyncDeleteConfig(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("dataId")
            String dataId, @RequestParam("group")
            String group, @RequestParam("content")
            String content, ModelMap modelMap) {

        List<String> datumIds = persistService.findDatumIdByContent(dataId, group, content);

		if (null != datumIds && datumIds.size() > 0) {
			for (String datumId : datumIds) {
				datumController.deleteSingleDatum(request, response, dataId, group, datumId, modelMap);
			}
		}
		response.setStatus(HttpServletResponse.SC_OK);
		return String.valueOf(HttpServletResponse.SC_OK);
    }


    /**
     * ���ӻ���·Ǿۺ����ݡ�
     */
    @RequestMapping(params = "method=syncUpdateAll", method = RequestMethod.POST)
    public String syncUpdateConfigAll(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("dataId")
            String dataId, @RequestParam("group")
            String group, @RequestParam("content")
            String content, ModelMap modelMap) {
        final String srcIp = RequestUtil.getRemoteIp(request);
        
        if (!checkParam(dataId, group, "datumId", content, response, modelMap)) {
            return "/common/error_message";
        }

        if (AggrWhitelist.isAggrDataId(dataId)) {
            log.warn("[aggr-conflict] {} attemp to publish single data, {}, {}", new Object[] {
                    RequestUtil.getRemoteIp(request), dataId, group });

            modelMap.addAttribute("content", dataId + " IS aggr");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return HttpServletResponse.SC_FORBIDDEN + "";
        }

        final Timestamp time = TimeUtils.getCurrentTime();

        ConfigInfo oldConfigInfo = persistService.findConfigInfo(dataId, group);
        if (oldConfigInfo == null) {
            persistService.addConfigInfo(dataId, group, content, srcIp, null, time);
        } else {
            persistService.updateConfigInfo(dataId, group, content, srcIp, null, time);
        }

        ConfigTraceService.logPersistenceEvent(dataId, group, time.getTime(), SystemConfig.LOCAL_IP, ConfigTraceService.PERSISTENCE_EVENT_PUB, content);

        response.setStatus(HttpServletResponse.SC_OK);
        return String.valueOf(HttpServletResponse.SC_OK);
    }
    
    /**
     * ���ӻ���·Ǿۺ����ݡ�
     */
    @RequestMapping(params = "method=updateAll", method = RequestMethod.POST)
    public String asyncUpdateConfigAll(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("dataId") String dataId, @RequestParam("group") String group,
            @RequestParam("content") String content, ModelMap modelMap) {
        return syncUpdateConfigAll(request, response, dataId, group, content, modelMap);
    }


    /**
     * �÷�������ɾ���ۺ����ݵ�datum����Ҳ����ɾ���Ǿۺϵ����ݡ�
     */
    @RequestMapping(params = "method=syncDeleteAll", method = RequestMethod.POST)
    public String syncDeleteConfigAll(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("dataId")
            String dataId, @RequestParam("group")
            String group, ModelMap modelMap) {
        return datumController.deleteAllDatum(request, response, dataId, group, modelMap);
    }
    /**
     * ɾ������������Ŀ
     */
    @RequestMapping(params = "method=deleteAll", method = RequestMethod.POST)
    public String asyncDeleteConfigAll(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("dataId")
            String dataId, @RequestParam("group")
            String group, ModelMap modelMap) {
        return datumController.deleteAllDatum(request, response, dataId, group, modelMap);
    }


    boolean checkParam(String dataId, String group, String datumId, String content, HttpServletResponse response,
            ModelMap modelMap) {
        boolean checkParamSuccess = true;
        String errorMessage = "";
        if (StringUtils.isBlank(dataId) || !ParamUtils.isValid(dataId.trim())) {
            checkParamSuccess = false;
            errorMessage = "invalid dataId";
        }
        else if (StringUtils.isBlank(group) || !ParamUtils.isValid(group)) {
            checkParamSuccess = false;
            errorMessage = "invalid group";
        }
        else if (StringUtils.isBlank(datumId) || !ParamUtils.isValid(datumId)) {
            checkParamSuccess = false;
            errorMessage = "invalid datumId";
        }
        else if (StringUtils.isBlank(content)) {
            checkParamSuccess = false;
            errorMessage = "invalid content";
        }

        if (!checkParamSuccess) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            modelMap.addAttribute("errorMsg", errorMessage);
        }

        return checkParamSuccess;
    }

}
