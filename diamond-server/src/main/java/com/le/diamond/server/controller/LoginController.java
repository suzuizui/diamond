package com.le.diamond.server.controller;

import javax.servlet.http.HttpServletRequest;

import com.le.diamond.server.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * ��¼�ǳ�������
 * 
 * @author boyan
 * @date 2010-5-6
 */
@Controller
@RequestMapping("/login.do")
public class LoginController {
    @Autowired
    private AdminService adminService;


    @RequestMapping(params = "method=login", method = RequestMethod.POST)
    public String login(HttpServletRequest request, @RequestParam("username") String username,
            @RequestParam("password") String password, ModelMap modelMap) {
        if (adminService.login(username, password)) {
            request.getSession().setAttribute("user", username);
            return "admin/admin";
        }
        else {
            modelMap.addAttribute("message", "��¼ʧ�ܣ��û������벻ƥ��");
            return "login";
        }
    }


    public AdminService getAdminService() {
        return adminService;
    }


    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }


    @RequestMapping(params = "method=logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "login";
    }
}
