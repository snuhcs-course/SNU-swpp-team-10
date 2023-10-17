package com.example.calendy.view.monthlyview;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;

import android.os.Bundle;

import com.example.calendy.R;
import com.example.calendy.view.monthlyview.decorator.OneDayDecorator;
import com.example.calendy.view.monthlyview.decorator.SaturdayDecorator;
import com.example.calendy.view.monthlyview.decorator.SundayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Calendar;
import java.util.List;

public class MonthlyView extends ComponentActivity {

    MaterialCalendarView materialCalendarView;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_view);
        materialCalendarView = findViewById(R.id.materialMonthlyView);




        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1))
                .setMaximumDate(CalendarDay.from(2030, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator);
    }


}