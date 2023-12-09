package com.swpp10.calendy.view.monthlyview.decorator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

import com.swpp10.calendy.data.maindb.plan.PlanType;
import com.swpp10.calendy.data.maindb.plan.PlanType;

import static com.swpp10.calendy.ui.theme.ColorKt.PriorityColor;

/**
 * Span to draw part of a
 */
public class MultiPlanSpan implements LineBackgroundSpan {

    private final int bgColor;
    private final int textColor;
    private final String title;
    private final int index;
    private final PlanType planType;

    public MultiPlanSpan(int index, int priority, String title, PlanType planType) {
        this.index = index;
        this.bgColor = PriorityColor(priority);
        if (priority >= 3) this.textColor= 0xffffffff;
        else this.textColor = 0xff000000;
        this.title=title;
        this.planType=planType;
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

        int w=right-left;
        int h=40;
        int xPos = 0 +w/2;
        int yPos = bottom + h/2 +h* (index) + 10;
        int p=4;

        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.
        if(planType==PlanType.SCHEDULE)
            canvas.drawRoundRect(left+p,bottom + h* (index) +p ,right-p,bottom +h* (index+1) -p,8,8,paint);
        else if(planType==PlanType.TODO)
        {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            canvas.drawRoundRect(left+p,bottom + h* (index) +p ,right-p,bottom +h* (index+1) -p,8,8,paint);
            paint.setStyle(Paint.Style.FILL);

//            paint.setColor(-1);
//            int stroke=5;
//            canvas.drawRoundRect(left+2+stroke,bottom + h* (index) +2+stroke ,right-2-stroke,bottom +h* (index+1) -2-stroke,3,3,paint);
        }

        paint.setColor(textColor);
//        paint.setFakeBoldText(true);
        canvas.drawText(title.substring(0,title.length()),xPos,yPos,paint);

        paint.setTextSize(oldTextSize);
        paint.setColor(oldColor);
        paint.setTextAlign(oldAlign);
//        paint.setFakeBoldText(false);
    }
}
