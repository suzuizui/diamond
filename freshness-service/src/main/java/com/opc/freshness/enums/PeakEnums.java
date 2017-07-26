package com.opc.freshness.enums;

import com.opc.freshness.common.util.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/20
 */
public enum PeakEnums {
    MORNING(1, "早高峰", "06:30:00", "10:30:00"),
    NOON(2, "午高峰", "10:30:00", "14:30:00"),
    EVENING(3, "晚高峰", "16:30:00", "23:59:00");
    //年月日
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_FORMAT2 = "yyyy-MM-dd HH:mm:ss";
    private Integer id;
    private String name;
    private String begin;
    private String end;

    PeakEnums(Integer id, String name, String begin, String end) {
        this.id = id;
        this.name = name;
        this.begin = begin;
        this.end = end;
    }

    public static PeakEnums getByValue(int value) {
        for (PeakEnums enums :PeakEnums.values()){
            if (enums.id==value){
                return enums;
            }
        }
        return null;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBegin() {
        return begin;
    }

    public Date getBeginDate(Date date) throws ParseException {
        return getDate(begin, date);
    }

    public String getEnd() {
        return end;
    }

    public Date getEndDate(Date date) throws ParseException {
        return getDate(end, date);
    }

    public static PeakEnums getByDate(Date date) {
        try {
            for (PeakEnums enums : PeakEnums.values()) {
                if (enums.getBeginDate(date).compareTo(date) <= 0
                        && enums.getEndDate(date).compareTo(date) >= 0) {
                    return enums;
                }
            }
        } catch (ParseException e) {
            return null;
        }
        return null;
    }

    private static Date getDate(String str, Date date) throws ParseException {
        return new SimpleDateFormat(DATE_FORMAT2).parse((DateUtils.format(date, DATE_FORMAT) + " " + str));
    }
}
