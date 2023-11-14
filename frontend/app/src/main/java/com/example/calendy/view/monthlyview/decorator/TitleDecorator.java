package com.example.calendy.view.monthlyview.decorator;

//import com.project.sample_calendar.R;
import com.example.calendy.R;
import com.example.calendy.data.maindb.plan.Plan;
import com.example.calendy.data.maindb.plan.Schedule;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

        import java.util.List;
import static com.example.calendy.utils.PlanHelperKt.getPlanType;

/**
 * Decorate several days with a dot
 */
public class TitleDecorator implements DayViewDecorator {

    private CalendarDay targetDay;
    private List<Plan> planList;
    public TitleDecorator(CalendarDay day, List<Plan> plans) {
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
            int planType=getPlanType(p);
            if(planType== PlanType.SCHEDULE){
                Schedule s=(Schedule)p;
                int dayLength = DateHelper.INSTANCE.getDiffBetweenDates(s.getStartTime(),s.getEndTime()) + 1;
                int dayOffset = DateHelper.INSTANCE.getDiffBetweenDates(s.getStartTime(),DateHelperKt.toDate(targetDay));
                view.addSpan(new SinglePlanSpan(count++,p.getPriority(),p.getTitle(),planType,dayLength,dayOffset));
            }
            else{
                view.addSpan(new SinglePlanSpan(count++,p.getPriority(),p.getTitle(),planType));
            }
            if(count==4) break; //hardcoded max viewable plan count
        }
    }
}