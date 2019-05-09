package com.ukasias.android.tutorial.graphic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.audiofx.DynamicsProcessing;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class PaintBoard extends View {
    final String TAG = "PaintBoard";

    Paint mPaint;
    Bitmap mBitmap;
    Canvas mCanvas;

    Context _context;

    int lastX;
    int lastY;

    public PaintBoard(Context context) {
        super(context);
        _context = context;
    }

    public PaintBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);  // 1080, 1584 <- 0, 0
        Log.d(TAG, "onSizeChanged(w: " + w
                + ", h: " + h
                + ", oldw: " + oldw
                + ", oldh: " + oldh);
        init(w, h);
    }

    public void init(int width, int height) {
        Log.d(TAG, "init()");
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(10F);

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        mCanvas = new Canvas();
        mCanvas.setBitmap(mBitmap);

        lastX = lastY = -1;
    }

    public void updatePaintProperty(int color, float strokewidth) {
        Log.d(TAG, "updatePaintProperty()");
        mPaint.setColor(color);
        mPaint.setStrokeWidth(strokewidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBitmap,0, 0, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int X = (int) event.getX();
        int Y = (int) event.getY();

        switch(action) {
            case MotionEvent.ACTION_UP:
                lastX = -1;
                lastY = -1;
                break;

            case MotionEvent.ACTION_DOWN:
                if (lastX != -1) {
                    if (X != lastX || Y != lastY) {
                        mCanvas.drawLine(lastX, lastY, X, Y, mPaint);
                    }
                }

                lastX = X;
                lastY = Y;

                break;

            case MotionEvent.ACTION_MOVE:
                if (lastX != -1) {
                    mCanvas.drawLine(lastX, lastY, X, Y, mPaint);
                }

                lastX = X;
                lastY = Y;

                break;

            default:
                break;
        }

        invalidate();

        return true;
    }
}
