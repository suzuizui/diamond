package com.le.diamond.utils;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;


public class AppNameUtilsUnitTest {

    /**
     * ������project.name������
     */
    @Test
    public void testGetAppNameByProjectName() {
        System.setProperty("project.name", "diamond-test");
        assertEquals("diamond-test", AppNameUtils.getAppName());
    }


    /**
     * ������SERVER HOME������
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
     * ���Դ�war���л�ȡӦ����
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
