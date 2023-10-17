package com.example.calendy.view.monthlyview;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;

import android.graphics.Color;
import android.os.Bundle;

import com.example.calendy.R;
import com.example.calendy.view.monthlyview.decorator.OneDayDecorator;
import com.example.calendy.view.monthlyview.decorator.SaturdayDecorator;
import com.example.calendy.view.monthlyview.decorator.SundayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;

import java.util.ArrayList;
import java.util.Calendar;

public class MonthlyView extends ComponentActivity {
    private final String TAG = this.getClass().getSimpleName();
    private  CalendarDay selectedDate = CalendarDay.today();
    MaterialCalendarView calendarView;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();

    private SelectedDayDecorator selectedDayDecorator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_view);
        calendarView = findViewById(R.id.materialMonthlyView);

        calendarView.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array.custom_months)));
        calendarView.setWeekDayFormatter(new ArrayWeekDayFormatter(getResources().getTextArray(R.array.custom_weekdays)));
        selectedDate=CalendarDay.today();
        calendarView.setSelectedDate(selectedDate);


        selectedDayDecorator = new SelectedDayDecorator(Color.BLUE,CalendarDay.today(),MonthlyView.this);

        calendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator,
                selectedDayDecorator
        );

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
              @Override
              public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                calendarView.removeDecorator(selectedDayDecorator);
                selectedDate=calendarView.getSelectedDate();
                selectedDayDecorator = new SelectedDayDecorator(Color.RED,selectedDate,MonthlyView.this);
                calendarView.addDecorators(selectedDayDecorator);
              }
          });

                calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2000, 0, 1))   //from 2000.1.1
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) //to 2030.12.31
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

    }
}

