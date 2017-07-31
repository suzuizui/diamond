package com.le.diamond.server.service;

import com.le.diamond.server.utils.LogUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p/>
 * Project: diamond-server
 * User: qiaoyi.dingqy
 * Date: 13-11-14
 * Time: ÉÏÎç10:18
 */
@Service
public class SwitchService {
    public static String SWITCH_KEY_ENABLE_ACCESSCONTROL = "enableAccessControl";
    private volatile Map<String, String> switches = new HashMap<String, String>();

    public SwitchService() throws IOException {
        loadSwitches();
        System.out.println(this.getSwitches());
    }

    public boolean getSwitchBoolean(String key, boolean defaultValue) {
        String status = switches.get(key);
        return status != null ? Boolean.valueOf(status) : defaultValue;
    }

    public void loadSwitches() throws IOException {
        File file = null;
        try {
            file = new File(PersistService.class.getResource("/switches.properties").toURI());
        } catch (URISyntaxException e) {
            LogUtil.defaultLog.warn("[reload-switches] exception {}", e);
            return;
        }

        Map<String, String> map = new HashMap<String, String>();

        for (String line : IOUtils.readLines(new InputStreamReader(new FileInputStream(file)))) {
            if (!StringUtils.isBlank(line) && !line.startsWith("#")) {
                String[] array = line.split("=");

                if (array == null || array.length != 2) {
                    LogUtil.defaultLog.error("corrupt switch record {}", line);
                    continue;
                }

                String key = array[0].trim();
                String value = array[1].trim();

                map.put(key, value);
            }
        }

        switches = map;

        LogUtil.defaultLog.warn("[reload-switches] {}", getSwitches());
    }

    public String getSwitches() {
        StringBuilder sb = new StringBuilder();

        String split = "";
        for (Map.Entry<String, String> entry : switches.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(split);
            sb.append(key);
            sb.append("=");
            sb.append(value);
            split = "; ";
        }

        return sb.toString();
    }

}
