package com.le.diamond.server.controller;

import com.le.diamond.server.service.SwitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * le.com Inc. Copyright (c) 1998-2101 All Rights Reserved.
 * <p/>
 * Project: diamond-server
 * User: qiaoyi.dingqy
 * Date: 13-11-14
 * Time: ÉÏÎç11:36
 */
@Controller
@RequestMapping("/switch.do")
public class SwitchController {
    @Autowired
    private SwitchService switchService;

    @RequestMapping(params = "method=reload", method = RequestMethod.GET)
    public String reload(HttpServletRequest request, HttpServletResponse response,
                         ModelMap modelMap) {
        try {
            switchService.loadSwitches();
            modelMap.addAttribute("content", switchService.getSwitches());
            response.setStatus(HttpServletResponse.SC_OK);
            return HttpServletResponse.SC_OK + "";
        } catch (Exception e) {
            modelMap.addAttribute("content", e.toString());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR + "";
        }
    }

    @RequestMapping(params = "method=list", method = RequestMethod.GET)
    public String list(HttpServletRequest request, HttpServletResponse response,
                         ModelMap modelMap) {
        try {
            modelMap.addAttribute("content", switchService.getSwitches());
            response.setStatus(HttpServletResponse.SC_OK);
            return HttpServletResponse.SC_OK + "";
        } catch (Exception e) {
            modelMap.addAttribute("content", e.toString());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR + "";
        }
    }
}