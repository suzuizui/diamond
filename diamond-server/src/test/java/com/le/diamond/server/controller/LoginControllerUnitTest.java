package com.le.diamond.server.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Ignore
public class LoginControllerUnitTest extends AbstractControllerUnitTest {

    private LoginController loginController;

    private Map<String, Object> sessionAttrMap;
    private HttpSession session;
    private HttpServletRequest request;
    private ModelMap modelMap;


    @Before
    public void setUp() throws Exception {
     
        this.loginController = new LoginController();
        this.loginController.setAdminService(adminService);

        this.sessionAttrMap = new HashMap<String, Object>();
        this.modelMap = new ModelMap();
    }


    @After
    public void tearDown() {
        this.sessionAttrMap.clear();
    }


    @Test
    public void testLoginSuccess() {
        assertEquals("admin/admin", this.loginController.login(request, "admin", "admin", modelMap));
        assertEquals("admin", request.getSession().getAttribute("user"));
    }


    @Test
    public void testLoginFail() {
        assertEquals("login", this.loginController.login(request, "boyan", "boyan", modelMap));
        assertNull(request.getSession().getAttribute("user"));
    }


    @Test
    public void testLoginSuccess_OtherUser() {
        this.adminService.addUser("boyan", "boyan");
        try {
            assertEquals("admin/admin", this.loginController.login(request, "boyan", "boyan", modelMap));
            assertEquals("boyan", request.getSession().getAttribute("user"));
        }
        finally {
            this.adminService.removeUser("boyan");
        }
    }


    @Test
    public void testLogout() {
        request.getSession().setAttribute("user", "admin");
        assertEquals("login", this.loginController.logout(request));
        assertNull(request.getSession().getAttribute("user"));
    }

}
