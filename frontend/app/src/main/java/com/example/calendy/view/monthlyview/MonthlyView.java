package com.example.calendy.view.monthlyview;

import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.calendy.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;

public class MonthlyView extends ComponentActivity {

    private final String TAG = this.getClass().getSimpleName();
    private MaterialCalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_view);

        calendarView = findViewById(R.id.materialMonthlyView);

        calendarView.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array.custom_months)));
        calendarView.setWeekDayFormatter(new ArrayWeekDayFormatter(getResources().getTextArray(R.array.custom_weekdays)));

    }
}