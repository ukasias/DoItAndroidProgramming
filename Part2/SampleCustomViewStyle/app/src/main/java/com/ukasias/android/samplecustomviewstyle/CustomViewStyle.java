package com.ukasias.android.samplecustomviewstyle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.view.View;

public class CustomViewStyle extends View {
    Paint paint;

    public CustomViewStyle(Context context) {
        super(context);

        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        canvas.drawRect(10, 10, 100, 100, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0F);
        paint.setColor(Color.GREEN);
        canvas.drawRect(120, 10, 210, 100, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setARGB(128, 0, 0, 255);
        canvas.drawRect(230, 10, 320, 100, paint);
        paint.setColor(Color.RED);

        DashPathEffect dash = new DashPathEffect(new float[] {5, 5}, 1);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3.0F);
        paint.setPathEffect(dash);
        paint.setColor(Color.GREEN);
        canvas.drawRect(340, 10, 430, 100, paint);

        paint = new Paint();
        paint.setColor(Color.MAGENTA);
        paint.setAntiAlias(false);
        canvas.drawCircle(250, 420, 200, paint);

        paint.setAntiAlias(true);
        canvas.drawCircle(680, 420, 200, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setColor(Color.BLACK);
        paint.setTextSize(100);
        canvas.drawText("Text(Stroke)", 20, 770, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(100);
        canvas.drawText("Text(fill)", 20, 900, paint);
    }
}
