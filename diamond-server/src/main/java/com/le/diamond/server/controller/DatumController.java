package com.le.diamond.server.controller;

import com.le.diamond.domain.ConfigInfoAggr;
import com.le.diamond.domain.Page;
import com.le.diamond.server.service.AggrWhitelist;
import com.le.diamond.server.service.PersistService;
import com.le.diamond.server.utils.AccumulateStatCount;
import com.le.diamond.server.utils.RequestUtil;
import com.le.diamond.server.service.merge.MergeDatumService;
import com.le.diamond.utils.ContentUtils;
import com.le.diamond.utils.ParamUtils;
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
import java.util.ArrayList;
import java.util.List;


/**
 * 增加/删除单条聚合数据的控制器。
 * 
 * @author jiuRen
 * 
 */
@Controller
@RequestMapping("/datum.do")
public class DatumController {
    
    private static final Logger log = LoggerFactory.getLogger(DatumController.class);
    
    @Autowired
    private PersistService persistService;
    
    @Autowired
    private MergeDatumService mergeService;
    
    public final AccumulateStatCount insertSuccessCount = new AccumulateStatCount();
    public final AccumulateStatCount insertFailureCount = new AccumulateStatCount();

    
    /**
     * 增加或更新单个数据条目。
     */
    @RequestMapping(params = "method=addDatum", method = RequestMethod.POST)
    public String addDatum(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("dataId")
            String dataId, //
            @RequestParam("group")
            String group, //
            @RequestParam("datumId")
            String datumId, //
            @RequestParam("content")
            String content, //
            ModelMap modelMap) {

        try {
        final String clientIp = RequestUtil.getRemoteIp(request);

        if (!checkParam(dataId, group, datumId, content, request, response, modelMap)) {
            return "/common/error_message";
        }

            // TODO 最好放在filter内，避免重复
            if (!AggrWhitelist.isAggrDataId(dataId)) {
                log.warn("[aggr-conflict] {} attemp to publish datum, {}, {}", new Object[] {
                        clientIp, dataId, group });

                modelMap.addAttribute("content", dataId + " ISNOT aggr");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return HttpServletResponse.SC_FORBIDDEN + "";
            }

        boolean isOK = persistService.addAggrConfigInfo(dataId, group, datumId, content);
            if (isOK) {
                log.info(
                        "[add-datum] success. clientIp={}, dataId={}, groupId={}, datumId={}, length={}, content={}",
                        new Object[] { clientIp, dataId, group, datumId, content.length(),
                                ContentUtils.truncateContent(content) });

                insertSuccessCount.increase();
                mergeService.addMergeTask(dataId, group);

                response.setStatus(HttpServletResponse.SC_OK);
                return HttpServletResponse.SC_OK + "";
            } else {
            log.warn(
                    "[add-datum] failed. dataId={}, groupId={}, datumId={}, content={}, clientIp={}",
                    new Object[] { dataId, group, datumId, ContentUtils.truncateContent(content),
                            clientIp });

            insertFailureCount.increase();
            modelMap.addAttribute("message", "add aggr config info, db affected row count illgal");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR + "";
        }
        } catch (RuntimeException e) {
            log.error(e.toString(), e);
            throw e;
        }
    }

    /**
     * 查询组下面的datum列表。
     */
    @RequestMapping(params = "method=listDatum", method = RequestMethod.GET)
    public String listDatum(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("dataId")
            String dataId, //
            @RequestParam("group")
            String group, //
            ModelMap modelMap) {
        
        // TODO 最好放在filter内，避免重复
        if (!AggrWhitelist.isAggrDataId(dataId)) {
            log.warn("[aggr-conflict] {} attemp to list datum, {}, {}", new Object[] {
                    RequestUtil.getRemoteIp(request), dataId, group });

            modelMap.addAttribute("content", dataId + " ISNOT aggr");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return HttpServletResponse.SC_FORBIDDEN + "";
        }

        //List<ConfigInfoAggr> datumList = persistService.findConfigInfoAggr(dataId, group);

        int PAGE_SIZE = 1000;
        List<ConfigInfoAggr> datumList = new ArrayList<ConfigInfoAggr>();
        int rowCount = persistService.aggrConfigInfoCount(dataId, group);
        int pageCount = (int) Math.ceil(rowCount * 1.0 / PAGE_SIZE);
        for (int pageNo = 1; pageNo <= pageCount; pageNo++) {
            Page<ConfigInfoAggr> page = persistService.findConfigInfoAggrByPage(dataId, group, pageNo, PAGE_SIZE);
            if (page != null) {
                datumList.addAll(page.getPageItems());
                log.info("[list-datum-query] {}, {}, size/total={}/{}", new Object[] {dataId, group,datumList.size(),rowCount});
            }
        }

        if (datumList.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return HttpServletResponse.SC_NOT_FOUND + "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (ConfigInfoAggr datum : datumList) {
            sb.append(datum.getDatumId());
            sb.append("=");
            sb.append(ContentUtils.truncateContent(datum.getContent()));
            sb.append("\r\n");
        }
        
        modelMap.addAttribute("content", sb.toString());
        response.setStatus(HttpServletResponse.SC_OK);
        return HttpServletResponse.SC_OK + "";
    }
    
    
    /**
     * 删除单条聚合前数据
     */
    @RequestMapping(params = "method=deleteDatum", method = RequestMethod.POST)
    public String deleteSingleDatum(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("dataId")
            String dataId, //
            @RequestParam("group")
            String group, //
            @RequestParam("datumId")
            String datumId, //
            ModelMap modelMap) {

        try {
        if (!checkParam(dataId, group, datumId, "rm-single", request, response, modelMap)) {
            return "/common/error_message";
        }

        // TODO 最好放在filter内，避免重复
            if (!AggrWhitelist.isAggrDataId(dataId)) {
                log.warn("[aggr-conflict] {} attemp to delete datum, {}, {}", new Object[] {
                        RequestUtil.getRemoteIp(request), dataId, group });

                modelMap.addAttribute("content", dataId + " ISNOT aggr");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return HttpServletResponse.SC_FORBIDDEN + "";
            }
        
        persistService.removeSingleAggrConfigInfo(dataId, group, datumId);
        mergeService.addMergeTask(dataId, group);

        response.setStatus(HttpServletResponse.SC_OK);
        return String.valueOf(HttpServletResponse.SC_OK);
        } catch (RuntimeException e) {
            log.error(e.toString(), e);
            throw e;
        }
    }

    /**
     * 同步删除某个dataId下面所有的聚合前数据
     */
    @RequestMapping(params = "method=deleteAllDatums", method = RequestMethod.POST)
    public String deleteAllDatum(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("dataId")
            String dataId, //
            @RequestParam("group")
            String group, //
            ModelMap modelMap) {

        try {
        if (!checkParam(dataId, group, "datumId", "rm", request, response, modelMap)) {
            return "/common/error_message";
        }

        persistService.removeAggrConfigInfo(dataId, group);
        mergeService.addMergeTask(dataId, group);
        
        response.setStatus(HttpServletResponse.SC_OK);
        return String.valueOf(HttpServletResponse.SC_OK);
        } catch (RuntimeException e) {
            log.error(e.toString(), e);
            throw e;
        }
    }

    boolean checkParam(String dataId, String group, String datumId, String content,
            HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        boolean checkParamSuccess = true;
        String errorMessage = "";
        if (StringUtils.isBlank(dataId) || !ParamUtils.isValid(dataId.trim())) {
            checkParamSuccess = false;
            errorMessage = "invalid dataId=" + dataId;
        }
        else if (StringUtils.isBlank(group) || !ParamUtils.isValid(group)) {
            checkParamSuccess = false;
            errorMessage = "invalid group=" + group;
        }
        else if (StringUtils.isBlank(datumId)) {
            checkParamSuccess = false;
            errorMessage = "empty datumId";
        }
        else if (StringUtils.isBlank(content)) {
            checkParamSuccess = false;
            errorMessage = "empty content";
        }

        if (!checkParamSuccess) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            modelMap.addAttribute("errorMsg", errorMessage);
            log.warn(errorMessage + ". clientIp=" + RequestUtil.getRemoteIp(request));
        }

        return checkParamSuccess;
    }

}
