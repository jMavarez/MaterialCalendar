package com.jmavarez.materialcalendar.Util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class CalendarDay implements Serializable {
    private final int day;
    private final int month;
    private final int year;

    private transient Calendar _calendar;

    private transient Date _date;

    public static CalendarDay today() {
        return from(CalendarUtils.getInstance());
    }

    @Deprecated
    public CalendarDay(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    @NonNull
    public static CalendarDay from(int day, int month, int year) {
        return new CalendarDay(day, month, year);
    }

    public static CalendarDay from(@Nullable Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return from(
                CalendarUtils.getDay(calendar),
                CalendarUtils.getMonth(calendar),
                CalendarUtils.getYear(calendar)
        );
    }

    public static CalendarDay from(@Nullable Date date) {
        if (date == null) {
            return null;
        }
        return from(CalendarUtils.getInstance(date));
    }

    public int getDay() {
        return this.day;
    }

    public int getMonth() {
        return this.month;
    }

    public int getYear() {
        return this.year;
    }

    @NonNull
    public Date getDate() {
        if (_date == null) {
            _date = getCalendar().getTime();
        }
        return _date;
    }

    @NonNull
    public Calendar getCalendar() {
        if (_calendar == null) {
            _calendar = CalendarUtils.getInstance();
            copyTo(_calendar);
        }
        return _calendar;
    }

    public void copyTo(@NonNull Calendar calendar) {
        calendar.clear();
        calendar.set(year, month, day);
    }

    @Override
    public String toString() {
        return "CalendarDay [" + day + "-" + month + "-" + year + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CalendarDay that = (CalendarDay) o;

        return day == that.day && month == that.month && year == that.year;
    }

    public boolean equalsMonth(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CalendarDay that = (CalendarDay) o;

        return month == that.month && year == that.year;
    }
}
