package com.le.diamond.server.controller;

import com.le.diamond.common.Constants;
import com.le.diamond.server.service.ConfigService;
import com.le.diamond.server.utils.GroupKey2;
import com.le.diamond.server.utils.LogUtil;
import com.le.diamond.server.utils.SingletonRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.le.diamond.common.Constants.LINE_SEPARATOR;
import static com.le.diamond.common.Constants.WORD_SEPARATOR;

// 轮询逻辑封装类
public class ConfigController {

    static public List<String> compareMd5(HttpServletRequest request,
                                          HttpServletResponse response, Map<String, String> clientMd5Map) {

        List<String> changedGroupKeys = new ArrayList<String>();

        for (Map.Entry<String, String> entry : clientMd5Map.entrySet()) {
            String groupKey = entry.getKey();
            String clientMd5 = entry.getValue();
            boolean isUptodate = ConfigService.isUptodate(groupKey, clientMd5);

            if (!isUptodate) {
                changedGroupKeys.add(groupKey);
            }
        }

        return changedGroupKeys;
    }

    static public String compareMd5OldResult(List<String> changedGroupKeys) {
        StringBuilder sb = new StringBuilder();

        for (String groupKey : changedGroupKeys) {
            String[] dataIdGroupId = GroupKey2.parseKey(groupKey);
            sb.append(dataIdGroupId[0]);
            sb.append(":");
            sb.append(dataIdGroupId[1]);
            sb.append(";");
        }
        return sb.toString();
    }

    static public String compareMd5ResultString(List<String> changedGroupKeys) throws IOException {
        if (null == changedGroupKeys) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (String groupKey : changedGroupKeys) {
            String[] dataIdGroupId = GroupKey2.parseKey(groupKey);
            sb.append(dataIdGroupId[0]);
            sb.append(WORD_SEPARATOR);
            sb.append(dataIdGroupId[1]);
            sb.append(LINE_SEPARATOR);
        }

        // 对WORD_SEPARATOR和LINE_SEPARATOR不可见字符进行编码, 编码后的值为%02和%01
        return URLEncoder.encode(sb.toString(), "UTF-8");
    }

    static public Map<String, String> getClientMd5Map(String configKeysString) {
        Map<String, String> md5Map = new HashMap<String, String>();

        if (null == configKeysString || "".equals(configKeysString)) {
            return md5Map;
        }

        for (String groupStr : configKeysString.split(LINE_SEPARATOR)) {
            String[] configKey = groupStr.split(WORD_SEPARATOR);
            // 必须是dataId+group+md5的形式, md5可以为空
            if (configKey.length > 3 || configKey.length < 2) {
                LogUtil.fatalLog.error("probe string format error: " + groupStr);
                continue;
            }

            String dataId = configKey[0];
            String group = configKey[1];
            String groupKey = GroupKey2.getKey(dataId, group);
            groupKey = SingletonRepository.DataIdGroupIdCache.getSingleton(groupKey);
            String md5 = (configKey.length == 3) ? configKey[2] : Constants.NULL;
            md5Map.put(groupKey, md5);
        }
        return md5Map;
    }

}

