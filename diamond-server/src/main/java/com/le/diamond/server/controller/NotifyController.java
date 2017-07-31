package com.le.diamond.server.controller;

import com.le.diamond.server.service.notify.NotifyService;
import com.le.diamond.server.service.dump.DumpService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 用于其他节点通知的控制器
 * 
 * @author boyan
 * @date 2010-5-7
 */
@Controller
@RequestMapping("/notify.do")
public class NotifyController {

    @Autowired
    private DumpService dumpService;

    /**
     * 通知配置信息改变
     *
     */
    @RequestMapping(method = RequestMethod.GET, params = "method=notifyConfigInfo")
    public String notifyConfigInfo(HttpServletRequest request, HttpServletResponse response,
                                   @RequestParam("dataId")String dataId,
                                   @RequestParam("group")String group) {
        dataId = dataId.trim();
        group = group.trim();

        String lastModified = request.getHeader(NotifyService.NOTIFY_HEADER_LAST_MODIFIED);
        long lastModifiedTs = StringUtils.isEmpty(lastModified) ? -1 : Long.parseLong(lastModified);

        String handleIp = request.getHeader(NotifyService.NOTIFY_HEADER_OP_HANDLE_IP);

        dumpService.dump(dataId, group, lastModifiedTs, handleIp);

        return String.valueOf(HttpServletResponse.SC_OK);
    }

}
