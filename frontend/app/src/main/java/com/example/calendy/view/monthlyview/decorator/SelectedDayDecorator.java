package com.example.calendy.view.monthlyview.decorator;

import android.app.Activity;
import android.graphics.drawable.Drawable;

//import com.project.sample_calendar.R;
import com.example.calendy.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

/**
 * Decorate several days with a dot
 */
public class SelectedDayDecorator implements DayViewDecorator {

    private final Drawable drawable;
    private CalendarDay selectedDate;

    public SelectedDayDecorator(CalendarDay date, Activity context) {
        drawable = context.getResources().getDrawable(R.drawable.more);
        this.selectedDate = date;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return selectedDate.equals(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(drawable);
    }
}