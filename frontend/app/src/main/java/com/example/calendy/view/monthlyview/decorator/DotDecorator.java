package com.example.calendy.view.monthlyview.decorator;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

//import com.project.sample_calendar.R;
import com.example.calendy.R;
import com.example.calendy.data.plan.Schedule;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Hashtable;
import java.util.List;

/**
 * Decorate several days with a dot
 */
public class DotDecorator implements DayViewDecorator {

    private int color;
    private Hashtable<CalendarDay, List<Schedule>> schedules;

    public DotDecorator(Hashtable<CalendarDay, List<Schedule>> schedules) {
        this.color = Color.DKGRAY;
        this.schedules=schedules;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {

        return schedules.containsKey(day);

    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(6, color)); // 날자밑에 점
    }
}