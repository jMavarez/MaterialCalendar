package com.jmavarez.materialcalendar;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jmavarez.materialcalendar.Util.CalendarDay;

public class DayView extends FrameLayout {
    public static final float DEFAULT_HEIGHT = 30.0f;
    private static final float DEFAULT_INDICATOR_MARGIN_BOTTOM = 2.0f;
    private static final float DEFAULT_INDICATOR_SIZE = 4.0f;
    private static final float DEFAULT_TEXT_SIZE = 12.0f;
    private static Drawable _indicatorDrawable;
    private static Drawable _selectionDrawable;
    private static Integer _indicatorMarginBottom;
    private static Integer _indicatorSize;
    private static Integer _measuredHeight;
    private static Integer _colorPrimary;

    static {
        _colorPrimary = null;
        _measuredHeight = null;
        _indicatorSize = null;
        _indicatorMarginBottom = null;
        _selectionDrawable = null;
        _indicatorDrawable = null;
    }

    private View indicator;
    private CalendarDay day;
    private boolean indicatorVisible;
    private boolean selected;
    private DisplayMetrics metrics;
    private TextView tvDay;

    public DayView(Context context) {
        super(context);
    }

    public DayView(Context context, CalendarDay day) {
        super(context);
        this.metrics = getResources().getDisplayMetrics();
        this.day = day;
        init();
    }

    public DayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DayView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private static Drawable generateCircleDrawable(int color) {
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(color);
        return drawable;
    }

    private void init() {
        this.selected = false;
        this.indicatorVisible = false;

        this.tvDay = new TextView(getContext());
        this.tvDay.setText(String.format("%d", new Object[]{Integer.valueOf(this.day.getDay())}));
        this.tvDay.setTextSize(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE);
        this.tvDay.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        this.tvDay.setGravity(Gravity.CENTER);
        LayoutParams params = new LayoutParams(getDefaultMeasuredHeight(), getDefaultMeasuredHeight());
        params.gravity = Gravity.CENTER_HORIZONTAL;
        this.tvDay.setLayoutParams(params);

        this.indicator = new View(getContext());
        LayoutParams params_indicator = new LayoutParams(getDefaultIndicatorSize(), getDefaultIndicatorSize());
        params_indicator.gravity = Gravity.BOTTOM + Gravity.CENTER_HORIZONTAL;
        params_indicator.setMargins(0, 0, 0, getDefaultIndicatorMarginBottom());
        this.indicator.setLayoutParams(params_indicator);

        addView(this.tvDay);
        addView(this.indicator);
    }

    public CalendarDay getDay() {
        return this.day;
    }

    public String getText() {
        return this.tvDay.getText().toString();
    }

    public boolean isSelected() {
        return this.selected;
    }

    @SuppressWarnings("deprecation")
    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            this.selected = selected;

            if (this.selected) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    this.tvDay.setBackgroundDrawable(getSelectionDrawable());
                } else {
                    this.tvDay.setBackground(getSelectionDrawable());
                }
                this.tvDay.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                this.indicator.setVisibility(View.GONE);
                this.tvDay.setTypeface(null, Typeface.BOLD);
                return;
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                this.tvDay.setBackgroundDrawable(null);
            } else {
                this.tvDay.setBackground(null);
            }

            this.tvDay.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            this.tvDay.setTypeface(null, Typeface.NORMAL);
            refreshIndicatorVisibility();
        }
    }

    @SuppressWarnings("deprecation")
    public void setIndicatorVisibility(boolean visible) {
        if (this.indicatorVisible != visible) {
            this.indicatorVisible = visible;

            if (this.indicatorVisible && this.indicator.getBackground() == null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    this.indicator.setBackgroundDrawable(getIndicatorDrawable());
                } else {
                    this.indicator.setBackground(getIndicatorDrawable());
                }
            }

            refreshIndicatorVisibility();
        }
    }

    private void refreshIndicatorVisibility() {
        View indicator = this.indicator;

        if (!this.selected && this.indicatorVisible) {
            indicator.setVisibility(View.VISIBLE);
            return;
        }

        indicator.setVisibility(View.GONE);
    }

    private int getDefaultMeasuredHeight() {
        if (_measuredHeight == null) {
            _measuredHeight = Integer.valueOf((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_HEIGHT, this.metrics));
        }
        return _measuredHeight.intValue();
    }

    private int getDefaultIndicatorSize() {
        if (_indicatorSize == null) {
            _indicatorSize = Integer.valueOf((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_INDICATOR_SIZE, this.metrics));
        }
        return _indicatorSize.intValue();
    }

    private int getDefaultIndicatorMarginBottom() {
        if (_indicatorMarginBottom == null) {
            _indicatorMarginBottom = Integer.valueOf((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_INDICATOR_MARGIN_BOTTOM, this.metrics));
        }
        return _indicatorMarginBottom.intValue();
    }

    private Drawable getSelectionDrawable() {
        if (_selectionDrawable == null) {
            _selectionDrawable = generateCircleDrawable(ContextCompat.getColor(getContext(), R.color.white));
        }
        return _selectionDrawable;
    }

    private Drawable getIndicatorDrawable() {
        if (_indicatorDrawable == null) {
            _indicatorDrawable = generateCircleDrawable(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
        return _indicatorDrawable;
    }
}
