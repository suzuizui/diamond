package com.le.diamond.server.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import com.le.diamond.server.utils.LogUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.le.diamond.server.utils.RegexParser;


/**
 * �ۺ����ݰ�������
 */
@Service
public class AggrWhitelist {

    /**
     * �ж�ָ����dataId�Ƿ��ھۺ�dataId��������
     */
    static public boolean isAggrDataId(String dataId) {
        if (null == dataId) {
            throw new IllegalArgumentException();
        }

        for (Pattern pattern : aggrDataIdWhitelist.get()) {
            if (pattern.matcher(dataId).matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * �������ݣ����¼��ؾۺϰ�����
     */
    static public void load(String content) {
        if (StringUtils.isBlank(content)) {
            LogUtil.fatalLog.error("aggr dataId whitelist is blank.");
            return;
        }
        LogUtil.defaultLog.warn("[aggr-dataIds] {}", content);
        
        try {
            List<String> lines = IOUtils.readLines(new StringReader(content));
            compile(lines);
        } catch (Exception ioe) {
            LogUtil.defaultLog.error("failed to load aggr whitelist, " + ioe.toString(), ioe);
        }
    }

    static void compile(List<String> whitelist) {
        List<Pattern> list = new ArrayList<Pattern>(whitelist.size());

        for (String line : whitelist) {
            if (!StringUtils.isBlank(line)) {
                String regex = RegexParser.regexFormat(line.trim());
                list.add(Pattern.compile(regex));
            }
        }
        aggrDataIdWhitelist.set(list);
    }

    static public List<Pattern> getWhiteList() {
        return aggrDataIdWhitelist.get();
    }
    
    // =======================

    static public final String AGGRIDS_METADATA = "com.le.diamond.metadata.aggrIDs";

    static final AtomicReference<List<Pattern>> aggrDataIdWhitelist = new AtomicReference<List<Pattern>>(
            new ArrayList<Pattern>());
}
