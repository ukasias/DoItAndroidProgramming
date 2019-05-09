package com.ukasias.android.samplemultitouch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View;

import java.net.ConnectException;

public class ImageDisplayView extends View implements View.OnTouchListener {
    private final String TAG = "ImageDisplayView";

    //----------------------------------------------------
    Context mContext;

    Canvas mCanvas;
    Paint mPaint;
    Matrix mMatrix;

    //----------------------------------------------------

    public float startX;
    public float startY;
    int lastX;
    int lastY;


    //----------------------------------------------------
    // 소스 비트맵
    Bitmap sourceBitmap;

    float sourceWidth = 0.0F;
    float sourceHeight = 0.0F;

    float  bitmapCenterX;
    float  bitmapCenterY;
    //----------------------------------------------------
    // 새로 보이게 될 비트맵
    Bitmap mBitmap;

    float displayWidth = 0.0F;
    float displayHeight = 0.0F;

    int displayCenterX = 0;
    int displayCenterY = 0;
    //----------------------------------------------------
    // 확대/축소
    public static float MAX_SCALE_RATIO = 5.0F;
    public static float MIN_SCALE_RATIO = 0.1F;

    float scaleRatio;
    float totalScaleRatio;

    float oldDistance = 0.0F;
    int oldPointerCount = 0;
    boolean isScrolling = false;
    float distanceThreshold = 3.0F;
    //----------------------------------------------------




    public ImageDisplayView(Context context) {
        super(context);
        mContext = context;

        init();
    }

    public ImageDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        init();
    }

    public void init() {
        mPaint = new Paint();
        mMatrix = new Matrix();

        lastX = -1;
        lastY = -1;

        setOnTouchListener(this);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "onSizeChanged() (" + oldw + "," + oldh + ") to ("
                + w + ", " + h + ")");

        if (w > 0 && h > 0) {
            newImage(w, h);

            redraw();
        }
    }

    /*
        새로운 비트맵 이미지를 메모리에 생성

        @param width
        @param height
     */
    public void newImage(int width, int height) {
        Bitmap img = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(img);

        mBitmap = img;
        mCanvas = canvas;

        displayWidth = (float) width;
        displayHeight = (float) height;

        displayCenterX = width/2;
        displayCenterY = height/2;
    }

    protected void drawBackground(Canvas canvas) {
        if (canvas != null) {
            canvas.drawColor(Color.BLACK);
        }
    }

    protected void onDraw(Canvas canvas) {
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0,0, null);
        }
    }

    public void redraw() {
        if (sourceBitmap == null) {
            Log.d(TAG, "sourceBitmap is null in redraw()");
            return;
        }

        drawBackground(mCanvas);

        float originX = (displayWidth - (float)sourceBitmap.getWidth()) / 2.0F;
        float originY = (displayHeight - (float)sourceBitmap.getHeight()) / 2.0F;

        mCanvas.translate(originX, originY);
        mCanvas.drawBitmap(sourceBitmap, mMatrix, mPaint);
        mCanvas.translate(-originX, -originY);

        invalidate();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final int action = motionEvent.getAction();

        int pointerCount = motionEvent.getPointerCount();
        Log.d(TAG, "Pointer Count: " + pointerCount);

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                if(pointerCount == 1) {
                    startX = motionEvent.getX();
                    startY = motionEvent.getY();
                }
                else if (pointerCount == 2) {
                    oldDistance = 0;
                    isScrolling = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (pointerCount == 1) {
                    if (isScrolling) {  // 두 손 터치 중이다가 한손이 되면 무시?
                        return true;
                    }

                    float curX = motionEvent.getX();
                    float curY = motionEvent.getY();

                    if (startX == 0.0F) {   // ????
                        startX = curX;
                        startY = curY;

                        return true;
                    }

                    float offsetX = curX - startX;
                    float offsetY = curY - startY;

                    if (oldPointerCount == 1) {
                        Log.d(TAG, "ACTION MOVE: " + offsetX + ", " + offsetY);

                        if (totalScaleRatio > 1.0F) {   // ????
                            moveImage(offsetX, offsetY);
                        }

                        startX = curX;
                        startY = curY;
                    }

                }
                else if (pointerCount == 2) {
                    float x1 = motionEvent.getX(0);
                    float y1 = motionEvent.getY(0);
                    float x2 = motionEvent.getX(1);
                    float y2 = motionEvent.getY(1);

                    float dx = x1 - x2;
                    float dy = y1 - y2;
                    float distance =
                            new Double(Math.sqrt(
                                    new Float(dx * dx + dy * dy).doubleValue())).floatValue();
                    float outScaleRatio = 0.0F;
                    if (oldDistance == 0.0F) {
                        oldDistance = distance;
                        break;  // 첫 움직임에는 반응을 하지 않아?
                    }

                    if (distance > oldDistance) { // 확대
                        if ((distance - oldDistance) < distanceThreshold) {
                            return true;
                        }
                        // ???? 이 계산이 맞냐?
                        outScaleRatio = scaleRatio + (oldDistance / distance * 0.05F);
                    }
                    else
                    if (distance < oldDistance) { // 축소
                        if ((oldDistance - distance) < distanceThreshold) {
                            return true;
                        }
                        // ???? 이 계산이 맞냐?
                        outScaleRatio = scaleRatio - (distance / oldDistance * 0.05F);
                    }

                    if (outScaleRatio < MIN_SCALE_RATIO ||
                            outScaleRatio > MAX_SCALE_RATIO) {
                        Log.d(TAG, "Invalid scaleRatio : " + outScaleRatio);
                    }
                    else {
                        Log.d(TAG, "Distance : " + distance + ", ScaleRatio: "
                        + outScaleRatio);
                        scaleImage(outScaleRatio);
                    }
                    oldDistance = distance;
                }

                oldPointerCount = pointerCount;
                break;
            case MotionEvent.ACTION_UP:
                if (pointerCount == 1) {
                    if (oldPointerCount == 1) {
                        moveImage(motionEvent.getX() - startX,
                                motionEvent.getY() - startY);
                    }
                }
                else {
                    isScrolling = false;
                }
                break;
        }
        return true;
    }

    private void scaleImage(float inScaleRatio) {
        Log.d(TAG, "scaleImage() called: " + inScaleRatio);

        mMatrix.postScale(inScaleRatio, inScaleRatio, bitmapCenterX, bitmapCenterY);
        mMatrix.postRotate(0);

        totalScaleRatio = totalScaleRatio * inScaleRatio;

        redraw();
    }

    private void moveImage(float offsetX, float offsetY) {
        Log.d(TAG, "moveImage() called: " + offsetX + ", " + offsetY);
        mMatrix.postTranslate(offsetX, offsetY);;

        redraw();
    }

    public void setImageData(Bitmap bitmap) {
        recycle();

        sourceBitmap = bitmap;

        sourceWidth = bitmap.getWidth();
        sourceHeight = bitmap.getHeight();

        bitmapCenterX = sourceBitmap.getWidth()/2;
        bitmapCenterY = sourceBitmap.getHeight()/2;

        scaleRatio = 1.0F;
        totalScaleRatio = 1.0F;
    }

    public void recycle() {
        if (sourceBitmap != null) {
            sourceBitmap.recycle();
        }
    }
}
