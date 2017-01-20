package com.jmavarez.materialcalendar;

import android.util.Log;

import com.jmavarez.materialcalendar.Interface.DayViewDecorator;
import com.jmavarez.materialcalendar.Util.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.HashSet;

public class EventDecorator implements DayViewDecorator {
    private final int color;
    private final HashSet<CalendarDay> calendarDays;

    public EventDecorator(int color, HashSet<CalendarDay> calendarDays) {
        this.color = color;
        this.calendarDays = new HashSet<>(calendarDays);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        Log.d("Decorate: ", "Day " + new SimpleDateFormat("d/MM/yyyy").format(day.getDate()) + " " + calendarDays.contains(day));
        return calendarDays.contains(day);
    }

    @Override
    public void decorate(DayView view) {

    }
}
