package com.example.calendy.view.monthlyview.decorator;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

//import com.project.sample_calendar.R;
import com.example.calendy.R;
import com.example.calendy.data.maindb.plan.Plan;
import com.example.calendy.data.maindb.plan.Schedule;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Hashtable;
import java.util.List;

/**
 * Decorate several days with a dot
 */
public class TitleDecorator implements DayViewDecorator {

    private int color;
    private CalendarDay targetDay;
    private List<Plan> planList;
    public TitleDecorator(CalendarDay day, List<Plan> plans) {
        this.color = Color.DKGRAY;
        this.targetDay = day;
        this.planList=plans;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return targetDay.equals(day);

    }

    @Override
    public void decorate(DayViewFacade view) {
//        view.addSpan(new DotSpan(6, color)); // 날자밑에 점
        int count=0;
        for(Plan p : planList){
            view.addSpan(new SinglePlanSpan(color,p.getTitle(),count++));
            if(count==2) break; //hardcoded max viewable plan count
        }
    }
}