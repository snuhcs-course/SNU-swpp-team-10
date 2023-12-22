package com.swpp10.calendy.view.monthlyview.decorator;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

//import com.project.sample_calendar.R;
import com.swpp10.calendy.R;
import com.swpp10.calendy.data.maindb.plan.Plan;
import com.swpp10.calendy.data.maindb.plan.Schedule;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import com.swpp10.calendy.data.maindb.plan.Plan;

import java.util.Hashtable;
import java.util.List;

/**
 * Decorate several days with a dot
 */
public class DotDecorator implements DayViewDecorator {

    private int color;
    private Hashtable<CalendarDay, List<Plan>> schedules;

    public DotDecorator(Hashtable<CalendarDay, List<Plan>> plans) {
        this.color = Color.DKGRAY;
        this.schedules=plans;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {

        return schedules.containsKey(day);

    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(6, color)); // 날자밑에 점
//        view.addSpan(new SinglePlanSpan(color,"title",1));
    }
}