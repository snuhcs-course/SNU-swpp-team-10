package com.example.calendy.view.weeklyview;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.calendy.R;
import com.example.calendy.data.plan.Schedule;
import com.example.calendy.view.weeklyview.decorator.SaturdayDecorator;
import com.example.calendy.view.weeklyview.decorator.SundayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

public class WeeklyView extends ComponentActivity {
    private final String TAG = this.getClass().getSimpleName();
    private  CalendarDay selectedDate = CalendarDay.today();
    MaterialCalendarView calendarView;
    private WeeklyViewModel model;
    //temp
    private Hashtable<CalendarDay, List<Schedule>> schedulesOfMonth;
    ActivityResultLauncher<Intent> startActivityResult;
    //timetable layout
    TableLayout timeTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_view);
//        model = new ViewModelProvider(this).get(MonthlyViewModel.class);

        selectedDate=CalendarDay.today();

        // initial setting for calendar view
        calendarView = findViewById(R.id.materialWeeklyView);
        calendarView.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array.custom_months)));
        calendarView.setWeekDayFormatter(new ArrayWeekDayFormatter(getResources().getTextArray(R.array.custom_weekdays)));
        calendarView.setSelectedDate(selectedDate);
        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2000, 0, 1))   //from 2000.1.1
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) //to 2030.12.31
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit();

        //styling
        calendarView.addDecorators(
                new SundayDecorator()
                ,new SaturdayDecorator()
        );
        calendarView.setTileHeightDp(40);

        //timetable
        timeTable = findViewById(R.id.timeTable);
        initTimeTable();



        Schedule tmpData;

    }

    private void initTimeTable() {
        int numberOfHours = 24; // 24 hours in a day

        // Calculate the height for each row
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenDP = (int)getResources().getDisplayMetrics().density;
        int leftMargin = 40 * screenDP;
        int calendarHeight = 200 * screenDP;
        // 추후 네비게이션 바 추가 시
//        int navBarHeight = 0;
//        int resourceId = getResources().getIdentifier("navigation_bar_default_height", "dimen", getPackageName());
//        if (resourceId > 0) {
//            navBarHeight = getResources().getDimensionPixelSize(resourceId);
//        }
        int availableHeight = screenHeight - calendarHeight;
        int rowHeight = availableHeight / numberOfHours;
        int rowWidth = (screenWidth - leftMargin) / 8;

        for (int i = 0; i < numberOfHours; i++) {
            TableRow row = new TableRow(this);
            row.setGravity(Gravity.CENTER);
            row.setBackgroundResource(R.drawable.border);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(rowWidth, rowHeight);
            row.setLayoutParams(lp);

            TextView hourText = new TextView(this);
            hourText.setText("    " + i + "  ");
            hourText.setGravity(Gravity.CENTER);
            hourText.setBackgroundResource(R.drawable.border);
            hourText.setLayoutParams(new TableRow.LayoutParams(leftMargin, rowHeight)); // Set the height for this cell
            row.addView(hourText);

            // Add cells for each day of the week
            for (int j = 0; j < 7; j++) {
                TextView cell = new TextView(this);
                cell.setBackgroundResource(R.drawable.border);
                row.setLayoutParams(lp);
                row.addView(cell);
            }

            timeTable.addView(row);
        }
    }

}

