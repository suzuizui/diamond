package com.le.diamond.utils;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;


public class AppNameUtilsUnitTest {

    /**
     * 测试有project.name的情形
     */
    @Test
    public void testGetAppNameByProjectName() {
        System.setProperty("project.name", "diamond-test");
        assertEquals("diamond-test", AppNameUtils.getAppName());
    }


    /**
     * 测试有SERVER HOME的情形
     */
    @Test
    public void testGetAppNameByServerHome() {
        // jboss home
        System.setProperty("jboss.server.home.dir", "/home/admin/diamond-test/.default");
        assertEquals("diamond-test", AppNameUtils.getAppName());
        System.clearProperty("jboss.server.home.dir");
        // jetty home
        System.setProperty("jetty.home", "/home/admin/diamond-test/.default");
        assertEquals("diamond-test", AppNameUtils.getAppName());
        System.clearProperty("jetty.home");
        // tomcat home
        System.setProperty("catalina.home", "/home/admin/diamond-test/.default");
        assertEquals("diamond-test", AppNameUtils.getAppName());
        System.clearProperty("catalina.home");
    }


    /**
     * 测试从war包中获取应用名
     */
    @Test
    public void testGetAppNameByArchiveFile() {
        String classPath = AppNameUtils.class.getResource("/").getPath();
        File classFile = new File(classPath);
        String deployPath = classFile.getParentFile().getAbsolutePath();
        File warFile = new File(deployPath, "diamond-test.war");
        try {
            if (!warFile.exists()) {
                warFile.createNewFile();
            }

            assertEquals("diamond-test", AppNameUtils.getAppName());
        }
        catch (Exception e) {
            // ignore
        }
        finally {
            if (warFile.exists()) {
                warFile.delete();
            }
        }
    }

}
