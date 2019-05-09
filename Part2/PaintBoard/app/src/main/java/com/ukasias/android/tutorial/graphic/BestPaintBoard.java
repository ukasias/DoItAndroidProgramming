package com.ukasias.android.tutorial.graphic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Stack;

public class BestPaintBoard extends View {
    final String TAG = "BestPaintBoard";

    Paint mPaint;
    Bitmap mBitmap;
    Canvas mCanvas;

    Context _context;

    float lastX;
    float lastY;

    private final Path mPath = new Path();
    private int mInvalidateExtraBorder = 10;
    private float mCurveEndX;
    private float mCurveEndY;

    static final float TOUCH_TOLERANCE = 8;

    private static final boolean RENDERING_ANTIALIAS = true;
    private static final boolean DITHER_FLAG = true;

    public static int defaultColor = Color.BLACK;
    public static float defaultSize = 2.0f;

    Stack undos;
    public static int maxUndos = 10;

    public BestPaintBoard(Context context) {
        super(context);
        _context = context;
        init();
    }

    public BestPaintBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
        init();
    }

    public void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(RENDERING_ANTIALIAS);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(defaultColor);
        mPaint.setStrokeWidth(defaultSize);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setDither(DITHER_FLAG);

        lastX = lastY = -1;

        undos = new Stack();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);  // 1080, 1584 <- 0, 0
        Log.d(TAG, "onSizeChanged(w: " + w
                + ", h: " + h
                + ", oldw: " + oldw
                + ", oldh: " + oldh);
        newImage(w, h);
    }

    public void newImage(int width, int height) {
        mCanvas = new Canvas();
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(mBitmap);

        drawBackGround(mCanvas);

        invalidate();
    }

    public void drawBackGround(Canvas canvas) {
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
        }
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

        Rect rect;

        switch(action) {
            case MotionEvent.ACTION_UP:
                rect = touchUp(event);
                if (rect != null) {
                    invalidate(rect);
                }
                mPath.rewind();

                return true;

            case MotionEvent.ACTION_DOWN:
                rect = touchDown(event);
                if (rect != null) {
                    invalidate(rect);
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                rect = touchMove(event);
                if (rect != null) {
                    invalidate(rect);
                }
                return true;
        }
        return false;
    }

    private Rect touchDown(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        lastX = x;
        lastY = y;

        Rect mInvalidRect = new Rect();
        mPath.moveTo(x, y);

        mInvalidRect.set(
                (int) x - mInvalidateExtraBorder, (int) y - mInvalidateExtraBorder,
                (int) x + mInvalidateExtraBorder, (int) y + mInvalidateExtraBorder);
        mCurveEndX = x;
        mCurveEndY = y;

        mCanvas.drawPath(mPath, mPaint);

        return mInvalidRect;

    }

    private Rect touchMove(MotionEvent event) {
        Rect rect = processMove(event);

        return rect;
    }

    private Rect touchUp(MotionEvent event) {
        Rect rect = processMove(event);

        return rect;
    }

    private Rect processMove(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();

        final float dx = Math.abs(x - lastX);
        final float dy = Math.abs(y - lastY);

        Rect mInvalidRect = new Rect();
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mInvalidRect.set(
                    (int) mCurveEndX - mInvalidateExtraBorder,
                    (int) mCurveEndY - mInvalidateExtraBorder,
                    (int) mCurveEndX + mInvalidateExtraBorder,
                    (int) mCurveEndY + mInvalidateExtraBorder);
            float cX = mCurveEndX = (x + lastX) / 2;
            float cY = mCurveEndY = (y + lastY) / 2;

            mPath.quadTo(lastX, lastY, cX, cY);
            mInvalidRect.union(
                    (int) lastX - mInvalidateExtraBorder,
                    (int) lastY - mInvalidateExtraBorder,
                    (int) lastX + mInvalidateExtraBorder,
                    (int) lastY + mInvalidateExtraBorder);
            mInvalidRect.union(
                    (int) cX - mInvalidateExtraBorder,
                    (int) cY - mInvalidateExtraBorder,
                    (int) cX + mInvalidateExtraBorder,
                    (int) cY + mInvalidateExtraBorder);

            lastX = x;
            lastY = y;

            mCanvas.drawPath(mPath, mPaint);
        }

        return mInvalidRect;
    }
}
