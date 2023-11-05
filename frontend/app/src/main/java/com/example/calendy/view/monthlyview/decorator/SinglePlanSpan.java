package com.example.calendy.view.monthlyview.decorator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

/**
 * Span to draw a dot centered under a section of text
 */
public class SinglePlanSpan implements LineBackgroundSpan {

    private final int color;
    private final String title;
    private final int index;

    public SinglePlanSpan(int color,String title, int index) {
        this.color = color;
        this.title=title;
        this.index = index;
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


        paint.setColor(0xff54ecf7);
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.LEFT);

        int w=right-left;
        int h=40;
        int xPos = 0 + 10;
        int yPos = bottom + h/2 +h* (index) + 10;
        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.

        canvas.drawRect(left+2,bottom + h* (index) +2 ,right-2,bottom +h* (index+1) -2,paint);
        if (color != 0) {paint.setColor(color);}
        canvas.drawText(title.substring(0,title.length()),xPos,yPos,paint);

        paint.setTextSize(oldTextSize);
        paint.setColor(oldColor);
        paint.setTextAlign(oldAlign);
    }
}
