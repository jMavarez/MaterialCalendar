package com.example.josue.materialcalendar;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jmavarez.materialcalendar.CalendarView;
import com.jmavarez.materialcalendar.Interface.OnDateChangedListener;
import com.jmavarez.materialcalendar.Interface.OnMonthChangedListener;
import com.jmavarez.materialcalendar.Util.CalendarDay;
import com.jmavarez.materialcalendar.Util.CalendarUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {
    CalendarView calendarView;
    TextView dateString;
    View emptyEvents;
    HashSet<CalendarDay> calendarDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        dateString = (TextView) findViewById(R.id.date);
        emptyEvents = findViewById(R.id.vEmpty);

//        emptyEvents.setVisibility(View.GONE);
        calendarView.setIndicatorsVisibility(true);

        calendarDays = new HashSet<>();
        CalendarDay calendarDay = CalendarDay.from(new Date());

        // Testing Calendar indicators
        for (int i = 1; i < CalendarUtils.getEndOfMonth(calendarDay.getCalendar()) + 1; i++) {
            if (i % 2 == 0) {
                CalendarDay day = CalendarDay.from(i, calendarDay.getMonth() + 1, calendarDay.getYear());
                calendarDays.add(day);
            }
        }

        calendarView.addEvents(calendarDays);

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(Date date) {
                String month = new SimpleDateFormat("MMMM yyyy").format(date);
                setToolbarTitle(month);
            }
        });

        calendarView.setOnDateChangedListener(new OnDateChangedListener() {
            @Override
            public void onDateChanged(Date date) {
                String d = new SimpleDateFormat("dd/MM/yyyy").format(date);
                dateString.setText(d);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.reset();
            }
        });

        String date = new SimpleDateFormat("MMMM yyyy").format(calendarView.getDateSelected());
        setToolbarTitle(date);
        String d = new SimpleDateFormat("dd/MM/yyyy").format(calendarView.getDateSelected());
        dateString.setText(d);
    }

    void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

        }

        return super.onOptionsItemSelected(item);
    }
}
