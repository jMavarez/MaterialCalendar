package com.jmavarez.materialcalendar;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jmavarez.materialcalendar.Interface.CalendarCallback;
import com.jmavarez.materialcalendar.Interface.OnDateChangedListener;
import com.jmavarez.materialcalendar.Util.CalendarDay;
import com.jmavarez.materialcalendar.Util.CalendarUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class MonthView extends ViewGroup {
    private static final int DEFAULT_DAYS_IN_WEEK = 7;
    final OnClickListener dayClickListener;
    final DisplayMetrics metrics;
    private final boolean starsOnSunday;
    private CalendarCallback callback;
    private ArrayList<DayView> dayViews;
    private OnDateChangedListener mListener;
    private CalendarDay calendarDay;
    private int offset;
    private int pagerPosition;

    public MonthView(Context context, CalendarDay calendarDay, boolean startsOnSunday, @NonNull CalendarCallback callback, OnDateChangedListener listener, int pagerPosition) {
        super(context);
        this.metrics = getResources().getDisplayMetrics();
        this.dayClickListener = new ClickListener();
        this.calendarDay = calendarDay;
        this.mListener = listener;
        this.callback = callback;
        this.starsOnSunday = startsOnSunday;
        this.pagerPosition = pagerPosition;
        init();
    }

    public CalendarDay getCalendarDay() {
        return calendarDay;
    }

    public void setCalendarDay(CalendarDay calendarDay) {
        this.calendarDay = calendarDay;
    }

    private void init() {
        this.dayViews = new ArrayList();
        this.offset = CalendarUtils.getDayOfWeek(this.calendarDay.getCalendar(), this.starsOnSunday) - 1;
        setLayoutParams(new android.view.ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addHeaders();
        int lastDay = CalendarUtils.getEndOfMonth(this.calendarDay.getCalendar());
        for (int i = 1; i <= lastDay; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(this.calendarDay.getYear(), this.calendarDay.getMonth(), i);
            CalendarDay cur = CalendarDay.from(calendar);
            addDayView(cur);
        }
        refreshEvents();
    }

    public void refreshEvents() {
        if (this.callback != null && this.callback.getIndicatorsVisible() && this.callback.getEvents() != null && this.callback.getEvents().size() > 0) {
            Iterator it = this.dayViews.iterator();
            int year = this.calendarDay.getYear();
            int month = this.calendarDay.getMonth() + 1;

            while (it.hasNext()) {
                DayView v = (DayView) it.next();
                int day = v.getDay().getDay();
                boolean shouldDecorate = false;
                CalendarDay today = CalendarDay.from(day, month, year);

                for (int i = 0; i < this.callback.getEvents().size(); i++) {
                    CalendarDay event = this.callback.getEvents().get(i);
                    if (event.equals(today)) {
                        shouldDecorate = true;
                    }
                }

                v.setIndicatorVisibility(shouldDecorate);
            }
        }
    }

    private void addHeaders() {
        int i = 1;
        while (i <= DEFAULT_DAYS_IN_WEEK) {
            int actual = this.starsOnSunday ? i == 1 ? DEFAULT_DAYS_IN_WEEK : i - 1 : i;
            TextView textView = new TextView(getContext());

            try {
                textView.setText(new CalendarUtils.Day(Integer.valueOf(actual)).getShortName(getContext()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextColor(ContextCompat.getColor(this.getContext(), R.color.white));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.0f);
            textView.setAllCaps(true);
            addView(textView);
            i++;
        }
    }

    private void addDayView(CalendarDay day) {
        DayView dayView = new DayView(getContext(), day);
        dayView.setOnClickListener(this.dayClickListener);
        this.dayViews.add(dayView);
        addView(dayView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);

        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            throw new IllegalStateException("CalendarPagerView should never be left to decide it's size");
        }

        int measureTileWidth = (specWidthSize - (((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8.0f, this.metrics)) * 2)) / DEFAULT_DAYS_IN_WEEK;
        int measureTileHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30.0f, getResources().getDisplayMetrics());

        setMeasuredDimension(specWidthSize, (measureTileHeight * DEFAULT_DAYS_IN_WEEK) + ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20.0f, this.metrics)));

        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(MeasureSpec.makeMeasureSpec(measureTileWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(measureTileHeight, MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean b, int left, int top, int right, int bottom) {
        int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8.0f, this.metrics);
        int marginLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8.0f, this.metrics);
        int count = getChildCount();
        int offset = this.offset;
        int headerOffset = 0;
        int childTop = marginTop;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            int childLeft;
            if (child instanceof TextView) {
                childLeft = (width * headerOffset) + marginLeft;
                child.layout(childLeft, 0, childLeft + width, height);
                headerOffset++;
            } else if (child instanceof DayView) {
                childLeft = (width * offset) + marginLeft;
                child.layout(childLeft, height + childTop, childLeft + width, (height * 2) + childTop);
                offset++;
                if (offset >= DEFAULT_DAYS_IN_WEEK) {
                    offset = 0;
                    childTop += height;
                }
            }
        }
    }

    public void refreshSelection(int day, int month, int year) {
        boolean select;
        if (this.calendarDay.getMonth() == month && this.calendarDay.getYear() == year) {
            select = true;
        } else {
            select = false;
        }

        Iterator it = this.dayViews.iterator();

        while (it.hasNext()) {
            boolean z;

            DayView v = (DayView) it.next();

            if (select && v.getDay().getDay() == day) {
                z = true;
            } else {
                z = false;
            }

            v.setSelected(z);
        }
    }

    public void hideIndicators() {
        Iterator it = this.dayViews.iterator();

        while (it.hasNext()) {
            ((DayView) it.next()).setIndicatorVisibility(false);
        }
    }

    class ClickListener implements OnClickListener {
        ClickListener() {
        }

        @Override
        public void onClick(View view) {
            if (view instanceof DayView) {
                CalendarDay day = ((DayView) view).getDay();
                if (day != null) {
                    view.setSelected(true);
                    if (MonthView.this.mListener != null) {
                        MonthView.this.mListener.onDateChanged(day.getDate());
                    }
                }
            }
        }
    }
}
