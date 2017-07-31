package com.le.diamond.server.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.le.diamond.server.service.PersistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/db.do")
public class DataBaseController {

    @Autowired
    private PersistService persistService;

    
    @RequestMapping(params = "method=getCurrentDB", method = RequestMethod.GET)
    public String getCurrentDBUrl(HttpServletRequest request, HttpServletResponse response,
            ModelMap modelMap) {
        modelMap.addAttribute("content", persistService.getCurrentDBUrl());
        response.setStatus(HttpServletResponse.SC_OK);
        return HttpServletResponse.SC_OK + "";
    }

    @RequestMapping(params = "method=reload", method = RequestMethod.GET)
    public String reload(HttpServletRequest request, HttpServletResponse response,
            ModelMap modelMap) {
        try {
            persistService.reload();
            modelMap.addAttribute("content", persistService.getCurrentDBUrl());
            response.setStatus(HttpServletResponse.SC_OK);
            return HttpServletResponse.SC_OK + "";
        } catch (Exception e) {
            modelMap.addAttribute("content", e.toString());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR + "";
        }
    }
}
