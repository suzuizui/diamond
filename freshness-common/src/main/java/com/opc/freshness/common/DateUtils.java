package com.opc.freshness.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * * 日期工具类 线程安全的时间格式化工具类
 * 
 * @author ming.wei
 * @date 2017年5月29日
 */
public final class DateUtils {

    private static final Logger logger = Logger.getLogger(DateUtils.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private DateUtils() {

    }

    /**
     * 时间格式化， 传入DATE
     * 
     * @param time Date型
     * @param format 时间格式 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String format(Date time, String format) {
        sdf.get().applyPattern(format);
        return sdf.get().format(time);
    }

    /**
     * 时间格式化， 传入字符串
     * 
     * @param time String型
     * @param format 时间格式 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String format(String time, String format) {
        sdf.get().applyPattern(format);
        try {
            Date date = sdf.get().parse(time);
            return sdf.get().format(date);
        } catch (ParseException e) {
            logger.error("日期格式化错误：传入日期参数【" + time + "】，格式化参数【" + format + "】\n" + e.getMessage());
        }
        return null;
    }

    /**
     * 时间格式化， 传入毫秒
     * 
     * @param time long型
     * @param format 时间格式 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String format(long time, String format) {
        sdf.get().applyPattern(format);
        return sdf.get().format(time);
    }

    /**
     * 格式化为 yyyy-MM-dd 格式
     * 
     * @param date
     * @return
     */
    public static Date format(String date) {
        sdf.get().applyPattern(DATE_FORMAT);
        try {
            return sdf.get().parse(date);
        } catch (ParseException e) {
            logger.error("日期格式化错误：传入日期参数【" + date + "】" + e.getMessage());
        }
        return null;
    }
    
    /**
     * 格式化为 format 格式
     * 
     * @param date
     * @return
     */
    public static Date parse(String date,String format) {
        sdf.get().applyPattern(format);
        try {
            return sdf.get().parse(date);
        } catch (ParseException e) {
            logger.error("日期格式化错误：传入日期参数【" + date + "】" + e.getMessage());
        }
        return null;
    }

    /**
     * 获取date之后day天的日期
     * 
     * @param date
     * @param day
     * @return
     */
    public static String getNextDate(String date, int day) {
        Date d = format(date);
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return format(now.getTime(), DATE_FORMAT);
    }

    /**
     * 传入日期，和本周周一日期做比较
     * 
     * @param date
     * @return
     */
    public static int compareToCurrentMonday(String date) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date d = cal.getTime();
        return format(d, DATE_FORMAT).compareTo(date);
    }

    /**
     * 
     * 对date进行天数n增加
     * 
     * @param date
     * @param n
     * @return
     */
    public static long addDay(String date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(format(date));
        cal.add(Calendar.DATE, n);
        return cal.getTimeInMillis();
    }

}
