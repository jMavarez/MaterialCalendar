package com.jmavarez.materialcalendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.jmavarez.materialcalendar.Interface.CalendarCallback;
import com.jmavarez.materialcalendar.Interface.DayViewDecorator;
import com.jmavarez.materialcalendar.Interface.OnDateChangedListener;
import com.jmavarez.materialcalendar.Interface.OnMonthChangedListener;
import com.jmavarez.materialcalendar.Util.CalendarDay;
import com.jmavarez.materialcalendar.Util.WrapContentViewPager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

public class CalendarView extends WrapContentViewPager implements OnDateChangedListener, CalendarCallback {
    private static final int PAGER_ITEMS = 60;
    private CalendarAdapter adapter;
    private OnDateChangedListener onDateChangedListener;
    private boolean indicatorsVisible;
    private OnMonthChangedListener onMonthChangedListener;
    private Date selection;
    private ArrayList<CalendarDay> eventDates;
    private int calendarColor;
    private boolean startsOnSunday;

    public CalendarView(Context context) {
        super(context);
        this.startsOnSunday = false;
        init();
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.startsOnSunday = false;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CalendarView, 0, 0);

        try {
            calendarColor = a.getColor(R.styleable.CalendarView_mc_color, ContextCompat.getColor(context, R.color.colorPrimary));
            startsOnSunday = a.getBoolean(R.styleable.CalendarView_mc_startsOnSunday, false);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        eventDates = new ArrayList<>();
        this.selection = new Date();
        this.indicatorsVisible = true;
        this.adapter = new CalendarAdapter(this, this);
        setOffscreenPageLimit(6);
        setAdapter(this.adapter);
        setCurrentItem(30, false);
        addOnPageChangeListener(new PageChangeListener());
        setBackgroundColor(calendarColor);
    }

    public void reset() {
        setCurrentItem(30, true);
        onDateChanged(new Date());
    }

    public void setDate(Date date) {
        onDateChanged(date);
    }

    @Override
    public Date getDateSelected() {
        return this.selection;
    }

    @Override
    public ArrayList<CalendarDay> getEvents() {
        return this.eventDates;
    }

    @Override
    public boolean getIndicatorsVisible() {
        return this.indicatorsVisible;
    }

    @Override
    public void onDateChanged(Date date) {
        this.selection = date;
        if (this.onDateChangedListener != null) {
            this.onDateChangedListener.onDateChanged(date);
        }
        refreshSelection();
    }

    private void refreshSelection() {
        Calendar c = Calendar.getInstance();
        c.setTime(getDateSelected());

        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        for (int i = 0; i < this.adapter.getViews().size(); i++) {
            MonthView v = this.adapter.getViews().valueAt(i);
            if (v != null) {
                v.refreshSelection(day, month, year);
            }
        }
    }

    public void setIndicatorsVisibility(boolean visible) {
        if (this.indicatorsVisible != visible) {
            this.indicatorsVisible = visible;
            if (this.indicatorsVisible) {
                refreshEvents();
                return;
            }
            for (int i = 0; i < this.adapter.getViews().size(); i++) {
                MonthView v = this.adapter.getViews().valueAt(i);
                if (v != null) {
                    v.hideIndicators();
                }
            }
        }
    }

    private void refreshEvents() {
        for (int i = 0; i < this.adapter.getViews().size(); i++) {
            MonthView view = this.adapter.getViews().valueAt(i);
            if (view != null) {
                view.refreshEvents();
            }
        }
    }

    public void addEvent(CalendarDay date) {
        if (date == null) {
            return;
        }
        this.eventDates.add(date);
        refreshEvents();
    }

    public void addEvents(Collection dates) {
        if (dates == null) {
            return;
        }
        this.eventDates.addAll(dates);
        refreshEvents();
    }

    public void setOnDateChangedListener(OnDateChangedListener listener) {
        this.onDateChangedListener = listener;
    }

    public void setOnMonthChangedListener(OnMonthChangedListener listener) {
        this.onMonthChangedListener = listener;
    }

    // Needs work
    public void scrollToMonth(CalendarDay day) {
        for (int i = 0; i < this.adapter.getViews().size(); i++) {
            MonthView monthView = this.adapter.getViews().valueAt(i);
            if (monthView != null) {
                if (day.equalsMonth(monthView.getCalendarDay())) {
                    int pos = 30 + (day.getMonth() - CalendarDay.from(this.selection).getMonth());
                    setCurrentItem(pos);
                    onDateChanged(day.getDate());
                    return;
                }
            }
        }
    }

    class PageChangeListener extends SimpleOnPageChangeListener {
        PageChangeListener() {
        }

        public void onPageScrollStateChanged(int state) {
            if (state == 0) {
                CalendarView.this.adapter.onScroll();
            }
        }
    }

    private class CalendarAdapter extends PagerAdapter {
        public Date mDateStart;
        public SparseArrayCompat<MonthView> mViews;
        public ArrayList<DayViewDecorator> dayViewDecorators;
        private CalendarCallback mCallback;
        private OnDateChangedListener mListener;
        private int mPositionStart;

        public CalendarAdapter(CalendarCallback callback, OnDateChangedListener listener) {
            this.mListener = listener;
            this.mCallback = callback;
            this.mDateStart = new Date();
            this.mPositionStart = 30;
            this.mViews = new SparseArrayCompat();
            this.dayViewDecorators = new ArrayList<>();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Calendar c = Calendar.getInstance();
            c.setTime(this.mDateStart);
            c.add(Calendar.MONTH, position - this.mPositionStart);
            c.set(Calendar.DAY_OF_MONTH, 1);
            MonthView v = new MonthView(CalendarView.this.getContext(), CalendarDay.from(c), CalendarView.this.startsOnSunday, this.mCallback, this.mListener, getCurrentItem());
            c.setTime(CalendarView.this.getDateSelected());
            v.refreshSelection(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH), c.get(Calendar.YEAR));
            this.mViews.put(position, v);
            container.addView(v);
            return v;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            this.mViews.remove(position);
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public int getCount() {
            return CalendarView.PAGER_ITEMS;
        }

        public SparseArrayCompat<MonthView> getViews() {
            return this.mViews;
        }

        public void onScroll() {
            Calendar c = Calendar.getInstance();
            c.setTime(this.mDateStart);
            c.add(Calendar.MONTH, CalendarView.this.getCurrentItem() - this.mPositionStart);
            Log.d("onScroll", "" + CalendarView.this.getCurrentItem() + " " + this.mPositionStart);
            if (CalendarView.this.onMonthChangedListener != null) {
                CalendarView.this.onMonthChangedListener.onMonthChanged(c.getTime());
            }

            Log.d("onScroll", CalendarDay.from(c).toString() + " - " + (CalendarView.this.getCurrentItem() - this.mPositionStart) + " Month: " + (c.get(Calendar.MONTH) + 1));
        }
    }
}
