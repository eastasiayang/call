package com.yang.basic;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MyCalendarHelp {
    private static final String TAG = "MyCalendarHelp";
    final public int DATE_FORMAT_SQL = 1;
    final public int DATE_FORMAT_DISPLAY = 2;
    SimpleDateFormat df1 = new SimpleDateFormat("yyyy_MM_dd HH:mm");
    SimpleDateFormat df2 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    private Context context;

    public MyCalendarHelp(Context con) {
        context = con;
    }

    public Calendar StringToCalendar(String String) {
        Calendar temp = Calendar.getInstance();
        Date dt = null;
        try {
            if (String.contains("年")) {
                dt = df2.parse(String);
            } else {
                dt = df1.parse(String);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dt == null) {
            return temp;
        }
        temp.setTime(dt);
        temp.set(Calendar.SECOND, 0);
        temp.set(Calendar.MILLISECOND, 0);
        return temp;
    }

    public String CalendarToString(Calendar cal, int iType) {
        if (iType == DATE_FORMAT_SQL) {
            return df1.format(cal.getTime());
        } else {
            return df2.format(cal.getTime());
        }
    }

    public long getDiff(Calendar date1, Calendar date2) {
        long time1 = date1.getTimeInMillis();
        long time2 = date2.getTimeInMillis();
        if(time1 > time2){
            return time1-time2;
        }else{
            return time2-time1;
        }
    }

    public int getWeekDay(String str) {
        int iWeek = 1;
        if (str.equals(context.getString(R.string.Sunday))) {
            iWeek = 1;
        } else if (str.equals(context.getString(R.string.Monday))) {
            iWeek = 2;
        } else if (str.equals(context.getString(R.string.Tuesday))) {
            iWeek = 3;
        } else if (str.equals(context.getString(R.string.Wednesday))) {
            iWeek = 4;
        } else if (str.equals(context.getString(R.string.Thursday))) {
            iWeek = 5;
        } else if (str.equals(context.getString(R.string.Friday))) {
            iWeek = 6;
        } else if (str.equals(context.getString(R.string.Saturday))) {
            iWeek = 7;
        }
        return iWeek;
    }

    public String getWeekString(Calendar cal) {
        int iWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (iWeek == 1) {
            return context.getString(R.string.Sunday);
        } else if (iWeek == 2) {
            return context.getString(R.string.Monday);
        } else if (iWeek == 3) {
            return context.getString(R.string.Tuesday);
        } else if (iWeek == 4) {
            return context.getString(R.string.Wednesday);
        } else if (iWeek == 5) {
            return context.getString(R.string.Thursday);
        } else if (iWeek == 6) {
            return context.getString(R.string.Friday);
        } else if (iWeek == 7) {
            return context.getString(R.string.Saturday);
        }
        return null;
    }
    public String getDurationTime(Calendar start, Calendar end){
        long day, hour, minute, second;
        long lDuration;
        lDuration = (getDiff(start, end));
        day = lDuration / (24 * 60 * 60 * 1000);
        hour = (lDuration / (60 * 60 * 1000)) % 24;
        minute = (lDuration / (60 * 1000)) % 60;
        second = (lDuration / 1000) % 60;

        if (day != 0) {
            return day + context.getResources().getString(R.string.day);
        } else if (hour != 0) {
            return hour + context.getResources().getString(R.string.hour);
        } else if (minute != 0){
            return minute + context.getResources().getString(R.string.minute);
        }else {
            return second + context.getResources().getString(R.string.second);
        }
    }
}

