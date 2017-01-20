package com.jmavarez.materialcalendar.Interface;

import com.jmavarez.materialcalendar.DayView;
import com.jmavarez.materialcalendar.Util.CalendarDay;

public interface DayViewDecorator {
    boolean shouldDecorate(CalendarDay day);

    void decorate(DayView view);
}
