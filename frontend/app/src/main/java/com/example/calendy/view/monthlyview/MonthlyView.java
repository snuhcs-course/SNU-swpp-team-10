package com.example.calendy.view.monthlyview;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.calendy.R;
import com.example.calendy.data.Schedule;
import com.example.calendy.view.monthlyview.decorator.DotDecorator;
import com.example.calendy.view.monthlyview.decorator.OneDayDecorator;
import com.example.calendy.view.monthlyview.decorator.SaturdayDecorator;
import com.example.calendy.view.monthlyview.decorator.SelectedDayDecorator;
import com.example.calendy.view.monthlyview.decorator.SundayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import kotlinx.coroutines.flow.StateFlow;

public class MonthlyView extends ComponentActivity {
    private final String TAG = this.getClass().getSimpleName();
    private  CalendarDay selectedDate = CalendarDay.today();
    MaterialCalendarView calendarView;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();

    private MonthlyViewModel model;
    //temp
    private Hashtable<CalendarDay, List<Schedule>> schedulesOfMonth;
    private SelectedDayDecorator selectedDayDecorator;
    private DotDecorator dotDecorator;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_view);
//        model = new ViewModelProvider(this).get(MonthlyViewModel.class);

        selectedDate=CalendarDay.today();

        // initial setting for calendar view
        calendarView = findViewById(R.id.materialMonthlyView);
        calendarView.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array.custom_months)));
        calendarView.setWeekDayFormatter(new ArrayWeekDayFormatter(getResources().getTextArray(R.array.custom_weekdays)));
        calendarView.setSelectedDate(selectedDate);
        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2000, 0, 1))   //from 2000.1.1
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) //to 2030.12.31
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        // selected day decorator initialization
        selectedDayDecorator = new SelectedDayDecorator(CalendarDay.today(),MonthlyView.this);
        //temp dummy code
        schedulesOfMonth = new Hashtable<>();
        List<Schedule> daySchedule=new ArrayList<>();
        daySchedule.add(new Schedule("123","test", new Date(2023,9,11,18,00),new Date(2023,9,11,20,00),123,1,2));
        schedulesOfMonth.put(CalendarDay.from(2023,9,11),daySchedule);

        dotDecorator = new DotDecorator(schedulesOfMonth);
        calendarView.addDecorators(
                new SundayDecorator()
                ,new SaturdayDecorator()
                ,oneDayDecorator
                ,selectedDayDecorator
                ,dotDecorator
        );

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            // selected date changed
              @Override
              public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                calendarView.removeDecorator(selectedDayDecorator);
                selectedDate=calendarView.getSelectedDate();
                selectedDayDecorator = new SelectedDayDecorator(selectedDate,MonthlyView.this);
                calendarView.addDecorators(selectedDayDecorator);

              }
          });

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            // selected month changed
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

            }
        });


    }



}

