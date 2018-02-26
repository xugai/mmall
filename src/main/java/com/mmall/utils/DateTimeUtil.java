package com.mmall.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by rabbit on 2018/2/10.
 */
public class DateTimeUtil {

    //joda-time

    private static final String STANTARD_FORMAT = "yyyy-MM-dd HH:mm:ss";
    //str->Date
    public static Date strToDate(String dateTimeStr, String formateStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formateStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    //Date->str
    public static String dateToStr(Date date, String formateStr){
        if(date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formateStr);
    }

    //DEFAULT strtoDate
    public static Date strToDate(String dateTimeStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANTARD_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    //DEFAULT dateToStr
    public static String dateToStr(Date date){
        if(date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANTARD_FORMAT);
    }


}
