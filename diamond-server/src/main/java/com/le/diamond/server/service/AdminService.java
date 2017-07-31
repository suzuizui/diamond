package com.le.diamond.server.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.le.diamond.server.DiamondServiceException;
import com.le.diamond.utils.ResourceUtils;


/**
 * 管理服务
 * 
 * @author boyan
 * @date 2010-5-5
 */
@Service
public class AdminService {

    private static final Logger fatalLog = LoggerFactory.getLogger("fatalLog");
    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    private volatile Properties properties = new Properties();

    /**
     * user.properties的路径url
     */
    private URL url;


    public URL getUrl() {
        return url;
    }


    @PostConstruct
    public void loadUsers() {
        Properties tempProperties = new Properties();
        InputStream in = null;
        try {
            url = ResourceUtils.getResourceURL("user.properties");
            in = new FileInputStream(url.getPath());
            tempProperties.load(in);
        }
        catch (IOException e) {
            String errorMsg = "load user.properties error";
            fatalLog.error(errorMsg, e);
            throw new DiamondServiceException(errorMsg, e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {
                    log.error("close user.properties error, ignore it", e);
                }
            }
        }
        this.properties = tempProperties;
    }


    public synchronized boolean login(String userName, String password) {
        String passwordInFile = this.properties.getProperty(userName);
        if (passwordInFile != null)
            return passwordInFile.equals(password);
        else
            return false;
    }


    public synchronized boolean addUser(String userName, String password) {
        if (this.properties.containsKey(userName))
            return false;
        this.properties.put(userName, password);
        return saveToDisk();

    }


    private boolean saveToDisk() {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(url.getPath());
            this.properties.store(out, "add user");
            out.flush();
            return true;
        }
        catch (IOException e) {
            log.error("保存user.properties文件失败", e);
            return false;
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException e) {
                    log.error("关闭user.properties文件失败", e);
                }
            }
        }
    }


    public synchronized Map<String, String> getAllUsers() {
        Map<String, String> result = new HashMap<String, String>();
        Enumeration<?> enu = this.properties.keys();
        while (enu.hasMoreElements()) {
            String address = (String) enu.nextElement();
            String group = this.properties.getProperty(address);
            result.put(address, group);
        }
        return result;
    }


    public synchronized boolean updatePassword(String userName, String newPassword) {
        if (!this.properties.containsKey(userName))
            return false;
        this.properties.put(userName, newPassword);
        return saveToDisk();
    }


    public synchronized boolean removeUser(String userName) {
        if (this.properties.size() == 1)
            return false;
        if (!this.properties.containsKey(userName))
            return false;
        this.properties.remove(userName);
        return saveToDisk();
    }
}
