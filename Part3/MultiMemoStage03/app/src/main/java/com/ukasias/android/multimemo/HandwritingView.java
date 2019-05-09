package com.ukasias.android.multimemo;

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
import android.widget.Toast;

import java.io.OutputStream;
import java.util.Stack;

public class HandwritingView extends View {
    private static final String TAG = "HandwritingView";

    /**
     * Constant Values
     */
    private static final int MAX_UNDOS = 10;
    private final int mInvalidateExtraBorder = 10;
    private static final float TOUCH_TOLERANCE = 1;
    private static final boolean RENDERING_ANTIALIAS = true;
    private static final boolean DITHER_FLAG = true;

    private int mCertainColor = 0xff000000;
    private float mStrokeWidth = 2.0f;


    /**
     * For View Object
     */
    private Context context;

    /**
     * For Drawing Action
     */
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private Path mPath;

    private int bitmapWidth;
    private int bitmapHeight;
    private float lastX;
    private float lastY;
    private float mCurveEndX;
    private float mCurveEndY;

    /**
     * For Undo Action
     */
    Stack undos;

    /**
     * For newly will be saved or not
     */
    private boolean changed;

    public HandwritingView(Context context) {
        super(context);
        this.context = context;

        init();
    }

    public HandwritingView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;

        init();
    }

    private void init() {
        undos = new Stack();
        mPath = new Path();

        mPaint = new Paint();
        mPaint.setAntiAlias(RENDERING_ANTIALIAS);
        mPaint.setColor(mCertainColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setDither(DITHER_FLAG);

        lastX = lastY = -1;

        bitmapWidth = bitmapHeight = 0;

        print("init() called.");
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }


    /**
     * 시작점: onSizeChanged()
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        print("onSizeChanged(w: " + w + ", h: " + h
                + ", oldw: " + oldw + ", oldh: " + oldh + ")");
        if (w > 0 && h > 0) {
            newImage(w, h);
        }
    }

    private void newImage(int width, int height) {
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas();

        mCanvas.setBitmap(mBitmap);

        bitmapWidth = mBitmap.getWidth();
        bitmapHeight = mBitmap.getHeight();

        changed = false;

        drawBackground(mCanvas);

        invalidate();
    }

    private void drawBackground(Canvas canvas) {
        if (canvas != null) {
            canvas.drawColor(Color.argb(255, 255, 255, 255));
        }
    }

    public void updatePaintProperty(int color, int size) {
        print("updatePaintProperty(int color: " + color + ", size: " + size + ") called.");
        mPaint.setColor(color);
        mPaint.setStrokeWidth(size);
    }

    public Bitmap getImage() {
        return mBitmap;
    }

    public void setImage(Bitmap newImage) {
        changed = false;
    }

    /* 구현 내용이 이상함 ?? -> mBitmap이 mCanvas의 bitmap이 아니게 됨. */
    private void setImageSize(int width, int height, Bitmap newImage) {
        if (mBitmap != null) {
            /* newImage의 크기보다 기존 bitmap(mBitmap)의 크기가 크면 큰 쪽에 맞춰
               width/height를 정한다.*/
            if (width < mBitmap.getWidth())  width = mBitmap.getWidth();
            if (height < mBitmap.getHeight()) height = mBitmap.getHeight();

            if (width < 1 || height < 1) return;

            Bitmap img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas();
            drawBackground(canvas);

            if (newImage != null) {
                canvas.setBitmap(newImage);
            }

            if (mBitmap != null) {
                mBitmap.recycle();  // -> Bitmap 메모리 즉시 해제.
            }

            if (mCanvas != null) {
                mCanvas.restore();
                // -> Canvas를 사용하기(변형: 회전, 원점 이동 등..) 전 환경을 save() 함수를
                // 이용해서 저장한 후, 변경해서 그린 다음, Canvas를 다시 사용하기 전 환경으로 돌릴때
                // restore()를 사용한다. [stack이 이용됨]
            }

            mBitmap = img;
            bitmapWidth = mBitmap.getWidth();
            bitmapHeight = mBitmap.getHeight();
            mCanvas = canvas;

            clearUndo();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /* mBitmap을 그림 */
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        Rect rect;

        switch(action) {
            case MotionEvent.ACTION_UP:
                changed = true;

                rect = touchUp(event, false);
                if (rect != null) {
                    invalidate(rect);
                }
                mPath.rewind(); // ??

                return true;

            case MotionEvent.ACTION_DOWN:
                /* 화면을 눌러 무언가를 그리기 시작하기 전의 bitmap을 stack에 넣어둔다. */
                saveUndo();

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

        int border = mInvalidateExtraBorder;
        int left = (int)x - border < 0? 0 : ((int) x - border);
        int top = (int)y - border < 0? 0 : ((int) y - border);
        int right = (int)x + border >= bitmapWidth?
                            (bitmapWidth - 1) : ((int) x + border);
        int bottom = (int)y + border >= bitmapHeight?
                            (bitmapHeight - 1) : ((int) y + border);

        mInvalidRect.set(left, top, right, bottom);
        mCurveEndX = x;
        mCurveEndY = y;

        mCanvas.drawPath(mPath, mPaint);    //?? mPath는 왜?
        // 아직 그릴 필요 없는 거 아니냐?

        return mInvalidRect;
    }

    private Rect touchMove(MotionEvent event) {
        Rect rect = processMove(event);
        return rect;
    }

    private Rect touchUp(MotionEvent event, boolean cancel) {
        Rect rect = processMove(event);
        return rect;
    }

    private Rect processMove(MotionEvent event) {
        // up이나 move나 처리가 같다.

        final float x = event.getX();
        final float y = event.getY();

        final float dx = Math.abs(x - lastX);
        final float dy = Math.abs(y - lastY);

        Rect mInvalidRect = new Rect();

        if (dx < TOUCH_TOLERANCE && dy < TOUCH_TOLERANCE) {
            return mInvalidRect;
        }

        final int border = mInvalidateExtraBorder;
        mInvalidRect.set((int)mCurveEndX - border, (int)mCurveEndY - border,
                (int)mCurveEndX + border, (int) mCurveEndY + border);
        mInvalidRect.union((int) lastX - border, (int) lastY - border,
                (int) lastX + border, (int) lastY + border);

        /* ?? 두 점 사이의 중간점까지만 그림? */
        float cX = mCurveEndX = (x + lastX) / 2;
        float cY = mCurveEndY = (y + lastY) / 2;

        mPath.quadTo(lastX, lastY, cX, cY);

        mInvalidRect.union((int) cX - border, (int)cY - border,
                (int) cX + border, (int) cY + border);

        lastX = x;
        lastY = y;

        mCanvas.drawPath(mPath, mPaint);

        return mInvalidRect;
    }

    public void undo() {
        print("undo() called.");

        /* stack에 있는 것을 꺼내서 설정 후 버림(recycle) */

        Bitmap prev = null;

        try {
            prev = (Bitmap)undos.pop();
        }
        catch(Exception ex) {
            print("Exception: " + ex.getMessage());
        }

        if (prev != null) {
            drawBackground(mCanvas);
            mCanvas.drawBitmap(prev, 0, 0, mPaint);
            invalidate();

            prev.recycle();
        }
    }

    private void saveUndo() {
        print("saveUndo() called.");

        if (mBitmap == null) {
            return;
        }

        while(undos.size() >= MAX_UNDOS) {
            /* stack: 가장 마지막에 넣는 element의 index가 0,
                      가장 먼저 넣은 element의 index가 size - 1.
             */
            Bitmap i = (Bitmap) undos.get(undos.size() - 1);
            i.recycle();
            undos.remove(i);
        }

        Bitmap img = Bitmap.createBitmap(
                mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(img);
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);

        undos.push(img);
    }

    private void clearUndo() {
        while(true) {
            Bitmap prev = (Bitmap) undos.pop();
            if (prev != null) {
                prev.recycle();
            }
            else {
                return;
            }
        }
    }

    public boolean Save(OutputStream outstream) {
        try {
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
            invalidate();
        }
        catch(Exception e) {
            print("Error occured in Save()");
            return false;
        }

        return true;
    }
}
