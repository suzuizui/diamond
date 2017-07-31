package com.le.diamond.server.controller;

import com.le.diamond.common.Constants;
import com.le.diamond.server.utils.RequestUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


@Controller
@RequestMapping({"/config.co", "/config.do"})
public class ConfigServlet extends HttpServlet {

    private static final long serialVersionUID = 4339468526746635388L;

    @Autowired
    private ConfigServletInner inner;     // for aop

    /**
     * 比较MD5
     */
    @RequestMapping(method = RequestMethod.POST)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String probeModify = request.getParameter(Constants.PROBE_MODIFY_REQUEST);

        if (StringUtils.isBlank(probeModify)) {
            throw new IOException("invalid probeModify");
        }

        Map<String, String> clientMd5Map = ConfigController.getClientMd5Map(probeModify);

        // do long-polling
        inner.doPollingConfig(request, response, clientMd5Map, probeModify.length());
    }


    /**
     * 批量数据查询接口
     */
    @RequestMapping(params = "method=batchGetConfig", method = RequestMethod.POST)
    public String batchGetConfig(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam("dataIds") String dataIds, @RequestParam("group") String group,
                                 ModelMap modelMap) throws IOException, ServletException {
        // check params
        if (StringUtils.isBlank(dataIds)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            modelMap.addAttribute("errorMsg", "invalid dataIds");
            return "/common/error_message";
        }
        if (StringUtils.isBlank(group)) {
            group = Constants.DEFAULT_GROUP;
        }

        // do batch get configs
        inner.doBatchGetConfig(request, response, dataIds, group, modelMap);

        return "/admin/config/batch_result";
    }


    /**
     * 取数据
     */
    @RequestMapping(method = RequestMethod.GET)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // check params
        String dataId = request.getParameter("dataId");
        if (StringUtils.isBlank(dataId)) {
            forwardErrorPage(request, response, HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String group = request.getParameter("group");
        if (StringUtils.isBlank(group)) {
            group = Constants.DEFAULT_GROUP;
        }

        final String clientIp = RequestUtil.getRemoteIp(request);


        //long st = System.currentTimeMillis();
        // for aop
        inner.doGetConfig(request, response, dataId, group, clientIp);
        // monitor rt
        //ResponseMonitor.addConfigTime((System.currentTimeMillis() - st));
    }


    public static void forwardErrorPage(HttpServletRequest request, HttpServletResponse response,
                                        int errorCode) throws IOException, ServletException {
        response.setStatus(errorCode);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/" + errorCode + ".jsp");
        dispatcher.forward(request, response);
    }

}

