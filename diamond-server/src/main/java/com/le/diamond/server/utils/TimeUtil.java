package com.le.diamond.server.utils;

import org.apache.commons.lang.time.FastDateFormat;

import java.util.Calendar;
import java.util.Date;

/**
 * <p/>
 * Project: diamond-server
 * User: qiaoyi.dingqy
 * Date: 13-10-29
 * Time: обнГ1:45
 */
public class TimeUtil {
    static public String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.get(Calendar.HOUR);
        FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
        return format.format(c.getTime());
    }

    public static void main(String[] args) {
        System.out.println(TimeUtil.getCurrentTime());
    }
}
