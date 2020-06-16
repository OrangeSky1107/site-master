package com.wintone.site.utils;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * create by ths on 2020/6/11
 */
public class CalendarUtil {

    /**
     * 获取当前星期几 具体年月日
     * @param choice
     * 当 choice = 0 的时候 返回 今天是星期几，当choice = 1 的时候会返回年月日
     * @return
     */
    public static String transformDate(int choice){

        String date = "";

        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = "天";
        } else if ("2".equals(mWay)) {
            mWay = "一";
        } else if ("3".equals(mWay)) {
            mWay = "二";
        } else if ("4".equals(mWay)) {
            mWay = "三";
        } else if ("5".equals(mWay)) {
            mWay = "四";
        } else if ("6".equals(mWay)) {
            mWay = "五";
        } else if ("7".equals(mWay)) {
            mWay = "六";
        }

        switch (choice){
            case 0 :
                date =  "星期" + mWay;
                break;
            case 1 :
                date =  mMonth + "月" + mDay + "日";
                break;
        }

        return date;
    }
}
