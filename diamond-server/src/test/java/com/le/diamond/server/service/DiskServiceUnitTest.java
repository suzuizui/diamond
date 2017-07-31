package com.le.diamond.server.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;


@Ignore
public class DiskServiceUnitTest {

    private DiskUtil diskService;

    private ServletContext servletContext;

    private File tempFile;

    private String path;


    @Before
    public void setUp() throws IOException {
        this.tempFile = File.createTempFile("diskServiceTest", "tmp");
        this.path = tempFile.getParent();
        this.diskService = new DiskUtil();
    }





    @After
    public void tearDown() throws IOException {
        tempFile.delete();
    }
}
