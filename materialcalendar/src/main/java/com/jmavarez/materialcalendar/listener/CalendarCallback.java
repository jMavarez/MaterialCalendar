package com.jmavarez.materialcalendar.listener;

import com.jmavarez.materialcalendar.Util.CalendarDay;

import java.util.ArrayList;
import java.util.Date;

public interface CalendarCallback {
    Date getDateSelected();

    ArrayList<CalendarDay> getEvents();

    boolean getIndicatorsVisible();
}
