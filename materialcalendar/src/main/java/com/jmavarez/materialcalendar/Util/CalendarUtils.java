package com.jmavarez.materialcalendar.Util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jmavarez.materialcalendar.R;

import java.util.Calendar;
import java.util.Date;

public class CalendarUtils {

    public static Calendar getInstance(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        copyDateTo(calendar, calendar);
        return calendar;
    }

    public static Calendar getInstance() {
        Calendar calendar = Calendar.getInstance();
        copyDateTo(calendar, calendar);
        return calendar;
    }

    public static void setToFirstDay(Calendar calendar) {
        int year = getYear(calendar);
        int month = getMonth(calendar);
        calendar.clear();
        calendar.set(year, month, 1);
    }

    public static void copyDateTo(Calendar from, Calendar to) {
        int year = getYear(from);
        int month = getMonth(from);
        int day = getDay(from);
        to.clear();
        to.set(year, month, day);
    }

    public static int getDay(Calendar c) {
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static int getDayOfWeek(Calendar c, boolean startOnSunday) {
        return getConvertedDayOfWeek(c.get(Calendar.DAY_OF_WEEK), startOnSunday);
    }

    public static int getMonth(Calendar c) {
        return c.get(Calendar.MONTH);
    }

    public static int getYear(Calendar c) {
        return c.get(Calendar.YEAR);
    }

    public static int getEndOfMonth(Calendar c) {
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static int getConvertedDayOfWeek(int day, boolean startOnSunday) {
        if (startOnSunday) {
            return day;
        }
        return day == 1 ? 7 : day - 1;
    }

    public static class Day {
        private Integer mDay;

        public Day(@NonNull Integer day) throws Exception {
            this.mDay = null;
            if (day.intValue() < 1 || day.intValue() > 7) {
                throw new Exception("Day must be between 1 and 7");
            }
            this.mDay = day;
        }

        public Integer toInteger() {
            return this.mDay;
        }

        public String getShortName(Context context) {
            Integer res;
            switch (this.mDay.intValue()) {
                case 1:
                    res = Integer.valueOf(R.string.monday_name_short);
                    break;
                case 2:
                    res = Integer.valueOf(R.string.tuesday_name_short);
                    break;
                case 3:
                    res = Integer.valueOf(R.string.wednesday_name_short);
                    break;
                case 4:
                    res = Integer.valueOf(R.string.thursday_name_short);
                    break;
                case 5:
                    res = Integer.valueOf(R.string.friday_name_short);
                    break;
                case 6:
                    res = Integer.valueOf(R.string.saturday_name_short);
                    break;
                case 7:
                    res = Integer.valueOf(R.string.sunday_name_short);
                    break;
                default:
                    return "getDay";
            }
            return context.getResources().getString(res.intValue());
        }
    }
}
