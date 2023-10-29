package com.example.calendy.view.monthlyview.decorator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

/**
 * Span to draw a dot centered under a section of text
 */
public class SinglePlanSpan implements LineBackgroundSpan {

    /**
     * Default radius used
     */
    public static final float DEFAULT_RADIUS = 3;

    private final float radius;
    private final int color;
    private final String title;

    public SinglePlanSpan(float radius, int color,String title) {
        this.radius = radius;
        this.color = color;
        this.title=title;
    }

    @Override
    public void drawBackground(
            Canvas canvas, Paint paint,
            int left, int right, int top, int baseline, int bottom,
            CharSequence charSequence,
            int start, int end, int lineNum
    ) {
        int oldColor = paint.getColor();
        if (color != 0) {
            paint.setColor(color);
        }
        canvas.drawCircle((left + right) / 2, bottom + radius, radius, paint);
        charSequence="text";
        canvas.drawText(charSequence,0,charSequence.length(),0,0,paint);
        paint.setColor(oldColor);
    }
}
