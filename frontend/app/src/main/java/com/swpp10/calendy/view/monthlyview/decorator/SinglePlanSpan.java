package com.swpp10.calendy.view.monthlyview.decorator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

import com.swpp10.calendy.data.maindb.plan.Plan;
import com.swpp10.calendy.data.maindb.plan.PlanType;
import com.swpp10.calendy.data.maindb.plan.PlanType;

import static androidx.compose.ui.graphics.ColorKt.Color;
import static com.swpp10.calendy.ui.theme.ColorKt.PriorityColor;

/**
 * Span to draw a dot centered under a section of text
 */
public class SinglePlanSpan implements LineBackgroundSpan {

    private final int bgColor;
    private final int textColor;
    private final String title;
    private final int index;
    private final PlanType planType;
    private final int dayLength;
    private final int dayOffset;
    private final Boolean completed;

public SinglePlanSpan(int index, int priority, String title, PlanType planType, int dayLength, int dayOffset, Boolean completed){
        this.index = index;
        this.bgColor = PriorityColor(priority);
        if (planType==PlanType.SCHEDULE && priority >= 3) this.textColor= 0xffffffff;
        else this.textColor = completed? 0xff808080 : 0xff000000;
        this.title=title;
        this.planType=planType;
        this.dayLength=dayLength;
        this.dayOffset=dayOffset;
        this.completed=completed;
    }

    @Override
    public void drawBackground(
            Canvas canvas, Paint paint,
            int left, int right, int top, int baseline, int bottom,
            CharSequence charSequence,
            int start, int end, int lineNum
    ) {
        // TODO: resolve hard coded codes
        int oldColor = paint.getColor();
        float oldTextSize = paint.getTextSize();
        Paint.Align oldAlign = paint.getTextAlign();


        paint.setColor(bgColor);
        paint.setTextSize(24);
        paint.setTextAlign(Paint.Align.CENTER);

        int w=(right-left);
        int h=40;
        int p=4;
        int width = w*dayLength;
        int xPos = 0 +width/2 -w*dayOffset;
        int yPos = bottom + h/2 +h* (index) + 10;

        if(planType==PlanType.TODO) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
        }
        canvas.drawRoundRect(
                left - w*dayOffset +2,
                bottom + h* (index) +p ,
                left +width - w*dayOffset -2,
                bottom +h* (index+1) -p,
                8,8,paint);
        paint.setStyle(Paint.Style.FILL);


        paint.setColor(textColor);
        paint.setStrikeThruText(completed);
        canvas.drawText(title.substring(0,title.length()),xPos,yPos,paint);
        paint.setStrikeThruText(false);

        paint.setTextSize(oldTextSize);
        paint.setColor(oldColor);
        paint.setTextAlign(oldAlign);
//        paint.setFakeBoldText(false);
    }
}
