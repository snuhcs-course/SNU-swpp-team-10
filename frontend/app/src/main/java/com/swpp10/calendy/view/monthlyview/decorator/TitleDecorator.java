package com.swpp10.calendy.view.monthlyview.decorator;

//import com.project.sample_calendar.R;
import com.swpp10.calendy.R;
import com.swpp10.calendy.data.maindb.plan.Plan;
import com.swpp10.calendy.data.maindb.plan.PlanType;
import com.swpp10.calendy.data.maindb.plan.Schedule;
import com.swpp10.calendy.utils.DateHelper;
import com.swpp10.calendy.utils.DateHelperKt;
import com.swpp10.calendy.utils.PlanHelperKt;
import com.swpp10.calendy.view.monthlyview.ILabel;
import com.swpp10.calendy.view.monthlyview.LabelSlot;
import com.swpp10.calendy.view.monthlyview.PlanLabel;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.swpp10.calendy.data.maindb.plan.Plan;
import com.swpp10.calendy.data.maindb.plan.PlanType;

import java.util.List;
import static com.swpp10.calendy.utils.PlanHelperKt.getPlanType;

/**
 * Decorate several days with a dot
 */
public class TitleDecorator implements DayViewDecorator {

    private CalendarDay targetDay;
    private LabelSlot<Plan> planLabel;
    public TitleDecorator(CalendarDay day, LabelSlot<Plan> planSlot) {
        this.targetDay = day;
        this.planLabel=planSlot;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return targetDay.equals(day);

    }

    @Override
    public void decorate(DayViewFacade view) {
        int max=7;  //prevent rendering too many plans
        for(int i =0;i<max;i++){
            var label=planLabel.getItemAt(i);
            if(label == null) continue;

            PlanLabel pLabel=(PlanLabel)label;
            Plan plan=pLabel.getItem();
            int index=pLabel.getIndex();
            PlanType type= PlanHelperKt.getPlanType(plan);
            int length=pLabel.getWeight();
            int offset=DateHelperKt.getDiffBetweenDates(pLabel.getStartDate(),DateHelperKt.toDate(targetDay));
            Boolean completed=pLabel.getCompleted();
            SinglePlanSpan span = new SinglePlanSpan(
                    index,
                    plan.getPriority(),
                    plan.getTitle(),
                    type,
                    length,
                    offset,
                    completed
                    );
            view.addSpan(span);

        }

    }
}