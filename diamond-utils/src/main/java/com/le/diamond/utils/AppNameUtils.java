package com.le.diamond.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class AppNameUtils {

    private static final Logger log = Logger.getLogger(AppNameUtils.class);

    private static final String PARAM_MARKING_PROJECT = "project.name";
    private static final String PARAM_MARKING_JBOSS = "jboss.server.home.dir";
    private static final String PARAM_MARKING_JETTY = "jetty.home";
    private static final String PARAM_MARKING_TOMCAT = "catalina.home";

    private static final String LINUX_ADMIN_HOME = "/home/admin/";
    private static final String SERVER_JBOSS = "jboss";
    private static final String SERVER_JETTY = "jetty";
    private static final String SERVER_TOMCAT = "tomcat";
    private static final String SERVER_UNKNOWN = "unknown server";

    private static final String[] DEFAULT_EXCLUDES = { "jmx-console.war" };

    private static final String[] SUFFIXS = { ".ear", ".spring", ".war" };


    public static String getAppName() {
        String appName = null;

        appName = getAppNameByProjectName();
        if (appName != null) {
            return appName;
        }

        appName = getAppNameByServerHome();
        if (appName != null) {
            return appName;
        }

        appName = getAppNameByArchiveFile();
        if (appName != null) {
            return appName;
        }

        return appName;
    }


    private static String getAppNameByProjectName() {
        return System.getProperty(PARAM_MARKING_PROJECT);
    }


    private static String getAppNameByServerHome() {
        String serverHome = null;
        if (SERVER_JBOSS.equals(getServerType())) {
            serverHome = System.getProperty(PARAM_MARKING_JBOSS);
        }
        else if (SERVER_JETTY.equals(getServerType())) {
            serverHome = System.getProperty(PARAM_MARKING_JETTY);
        }
        else if (SERVER_TOMCAT.equals(getServerType())) {
            serverHome = System.getProperty(PARAM_MARKING_TOMCAT);
        }

        if (serverHome != null && serverHome.startsWith(LINUX_ADMIN_HOME)) {
            return StringUtils.substringBetween(serverHome, LINUX_ADMIN_HOME, "/");
        }

        return null;
    }


    private static String getServerType() {

        String serverType = null;

        if (System.getProperty(PARAM_MARKING_JBOSS) != null) {
            serverType = SERVER_JBOSS;
        }
        else if (System.getProperty(PARAM_MARKING_JETTY) != null) {
            serverType = SERVER_JETTY;
        }
        else if (System.getProperty(PARAM_MARKING_TOMCAT) != null) {
            serverType = SERVER_TOMCAT;
        }
        else {
            serverType = SERVER_UNKNOWN;
        }
        return serverType;
    }


    private static String getAppNameByArchiveFile() {
        return getAppNameByArchiveFile(DEFAULT_EXCLUDES);
    }


    private static String getAppNameByArchiveFile(String[] excludes) {
        String[] appNames = getAllAppNames(excludes);
        if (appNames == null || appNames.length == 0) {
            log.warn("not found [ear,spring,or war]");
            return null;
        }
        if (appNames.length > 1) {
            log.warn("found not one [ear,spring,or war]" + Arrays.toString(appNames) + ",use first");
        }
        return appNames[0];
    }


    private static String[] getAllAppNames(String[] excludes) {
        File classpath = getClasspath();
        File deployDir = classpath.getParentFile();
        List<String> appNames = new LinkedList<String>();
        for (String suffix : SUFFIXS) {
            File[] files = listFiles(deployDir, suffix, excludes);
            addFilesToAppNames(files, appNames, suffix);
        }
        return appNames.toArray(new String[appNames.size()]);
    }


    private static void addFilesToAppNames(File[] files, List<String> appNames, String suffix) {
        if (files != null) {
            for (File file : files) {
                String filename = file.getName();
                String appName = filename.substring(0, filename.length() - suffix.length());
                appNames.add(appName);
            }
        }
    }


    private static File[] listFiles(File dir, final String suffix, final String[] excludes) {
        return dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return !inExcludes(file, excludes) && file.getName().toLowerCase().endsWith(suffix);
            }
        });
    }


    private static boolean inExcludes(File file, String[] excludes) {
        for (String exclude : excludes) {
            if (file.getName().equalsIgnoreCase(exclude)) {
                return true;
            }
        }
        return false;
    }


    private static File getClasspath() {
        String classpath = AppNameUtils.class.getResource("/").getPath();
        log.info("The classpath is " + classpath);
        return new File(classpath);
    }
}
