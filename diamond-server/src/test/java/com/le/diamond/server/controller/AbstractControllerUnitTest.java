package com.le.diamond.server.controller;

import java.io.File;

import javax.servlet.ServletContext;

import com.le.diamond.server.service.AdminService;
import com.le.diamond.server.service.ConfigService;
import com.le.diamond.server.service.PersistService;
import org.junit.Ignore;


@Ignore
public class AbstractControllerUnitTest {

    protected PersistService persistService;
    protected ConfigService configService;
    protected AdminService adminService;

    protected ServletContext servletContext;
    protected File tempFile;
    protected String path;

    /**
     * ��Ԫ���Բ���Ҫ֪ͨ
     * 
     * @author leiwen.zh
     * 
     */
    private static class MockConfigService extends ConfigService {

    }

}
