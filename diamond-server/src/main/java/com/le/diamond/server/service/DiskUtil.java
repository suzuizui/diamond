package com.le.diamond.server.service;

import java.io.File;
import java.io.IOException;

import com.le.diamond.server.WebFilter;
import com.le.diamond.server.utils.LogUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.le.diamond.common.Constants;


/**
 * 磁盘操作工具类。
 * 
 * 只有一个dump线程。
 * 
 * @author jiuRen
 */
public class DiskUtil {
    
    /**
     * 保存配置信息到磁盘
     */
    static public void saveToDisk(String dataId, String group, String content) throws IOException {
        File targetFile = targetFile(dataId, group);
        FileUtils.writeStringToFile(targetFile, content, Constants.ENCODE);
    }

    /**
     * 删除磁盘上的配置文件
     */
    static public void removeConfigInfo(String dataId, String group) {
        FileUtils.deleteQuietly(targetFile(dataId, group));
    }

    /**
     * 返回服务端缓存文件的路径
     */
    static public File targetFile(String dataId, String group) {
        File file = new File(WebFilter.rootPath(), BASE_DIR);
        file = new File(file, group);
        file = new File(file, dataId);
        return file;
    }
    static public String relativePath(String dataId, String group) {
        return BASE_DIR + "/" + dataId + "/" + group;
    }

    static public void clearAll() {
        File file = new File(WebFilter.rootPath(), BASE_DIR);
        if (FileUtils.deleteQuietly(file)) {
            LogUtil.defaultLog.info("clear all config-info success.");
        } else {
            LogUtil.defaultLog.warn("clear all config-info failed.");
        }
    }

    // =====================
    static final Logger logger = LoggerFactory.getLogger(DiskUtil.class);
    static final String BASE_DIR = "config-data";
    
}
