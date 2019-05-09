package com.ukasias.android.samplecustomviewdrawable;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class CustomViewDrawable extends View {
    private ShapeDrawable upperDrawable;
    private ShapeDrawable lowerDrawable;

    public CustomViewDrawable(Context context) {
        super(context);

        WindowManager manager
                = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point sizePoint = new Point();
        display.getSize(sizePoint);
        int width = sizePoint.x;
        int height = sizePoint.y;

        Resources res = getResources();
        int blackColor = res.getColor(R.color.color01);
        int grayColor = res.getColor(R.color.color02);
        int darkGrayColor = res.getColor(R.color.color03);
        int whiteColor = res.getColor(R.color.color04);

        //--------------
        upperDrawable = new ShapeDrawable();
        RectShape rectangle = new RectShape();
        rectangle.resize(width, height * 2/3);
        upperDrawable.setShape(rectangle);
        upperDrawable.setBounds(0, 0, width, height * 2/3);

        LinearGradient gradient = new LinearGradient(0, 0, 0, height * 2/3,
                whiteColor, blackColor, Shader.TileMode.CLAMP);

        Paint paint = upperDrawable.getPaint();
        paint.setShader(gradient);

        lowerDrawable = new ShapeDrawable();
        RectShape rectangle2 = new RectShape();
        rectangle2.resize(width, height * 1/3);
        lowerDrawable.setShape(rectangle2);
        lowerDrawable.setBounds(0, height*2/3, width, height);

        LinearGradient gradient2 = new LinearGradient(0, 0, 0, height * 1/3,
                blackColor, whiteColor, Shader.TileMode.CLAMP);

        Paint paint2 = lowerDrawable.getPaint();
        paint2.setShader(gradient2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (upperDrawable != null) {
            upperDrawable.draw(canvas);
        }

        if (lowerDrawable != null) {
            lowerDrawable.draw(canvas);
        }

        Paint pathPaint = new Paint();
        pathPaint.setAntiAlias(true);
        pathPaint.setColor(Color.YELLOW);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeWidth(32.0F);
        pathPaint.setStrokeCap(Paint.Cap.BUTT);
        pathPaint.setStrokeJoin(Paint.Join.MITER);

        Path path = new Path();
        path.moveTo(20, 70);
        path.lineTo(420, 70);
        path.lineTo(380, 230);
        path.lineTo(460, 180);
        path.lineTo(540, 350);
        canvas.drawPath(path, pathPaint);


        pathPaint.setColor(Color.WHITE);
        pathPaint.setStrokeCap(Paint.Cap.ROUND);
        pathPaint.setStrokeJoin(Paint.Join.ROUND);

        path.offset(120, 400);
        canvas.drawPath(path, pathPaint);

        pathPaint.setColor(Color.CYAN);
        pathPaint.setStrokeCap(Paint.Cap.SQUARE);
        pathPaint.setStrokeJoin(Paint.Join.BEVEL);

        path.offset(120, 400);
        canvas.drawPath(path, pathPaint);
    }
}
