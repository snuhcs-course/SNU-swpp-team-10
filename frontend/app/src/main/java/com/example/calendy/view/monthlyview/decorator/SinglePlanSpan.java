package com.example.calendy.view.monthlyview.decorator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

import com.example.calendy.data.maindb.plan.Plan;
import com.example.calendy.data.maindb.plan.PlanType;

import static androidx.compose.ui.graphics.ColorKt.Color;
import static com.example.calendy.ui.theme.ColorKt.PriorityColor;

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

    public SinglePlanSpan(int index, int priority, String title, PlanType planType) {
        this.index = index;
        this.bgColor = PriorityColor(priority);
        if (priority > 3) this.textColor= 0xffffffff;
        else this.textColor = 0xff000000;
        this.title=title;
        this.planType=planType;
        this.dayLength=1;
        this.dayOffset=0;
    }
    public SinglePlanSpan(int index, int priority, String title, PlanType planType, int dayLength, int dayOffset) {
        this.index = index;
        this.bgColor = PriorityColor(priority);
        if (priority > 3) this.textColor= 0xffffffff;
        else this.textColor = 0xff000000;
        this.title=title;
        this.planType=planType;
        this.dayLength=dayLength;
        this.dayOffset=dayOffset;
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
        int width = w*dayLength;
        int xPos = 0 +width/2 -w*dayOffset;
        int yPos = bottom + h/2 +h* (index) + 10;
        int p=4;

        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.
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

//            paint.setColor(-1);
//            int stroke=5;
//            canvas.drawRoundRect(left+2+stroke,bottom + h* (index) +2+stroke ,right-2-stroke,bottom +h* (index+1) -2-stroke,3,3,paint);


        paint.setColor(textColor);
//        paint.setFakeBoldText(true);
        canvas.drawText(title.substring(0,title.length()),xPos,yPos,paint);

        paint.setTextSize(oldTextSize);
        paint.setColor(oldColor);
        paint.setTextAlign(oldAlign);
//        paint.setFakeBoldText(false);
    }
}
