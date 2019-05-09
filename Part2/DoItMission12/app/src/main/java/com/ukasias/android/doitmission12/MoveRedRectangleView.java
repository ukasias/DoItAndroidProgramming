package com.ukasias.android.doitmission12;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MoveRedRectangleView extends View {
    final String TAG = "MoveRedRectangleView";

    final int LENGTH_X = 90;
    final int LENGTH_Y = 60;

    Paint drawPaint;
    Paint erasePaint;
    Canvas mCanvas;
    Bitmap mBitmap;

    int display_width;
    int display_height;

    float _x;
    float _y;

    float startX;
    float startY;

    public MoveRedRectangleView(Context context) {
        super(context);
        init();
    }

    public MoveRedRectangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        drawPaint = new Paint();
        drawPaint.setColor(Color.RED);
        erasePaint = new Paint();
        erasePaint.setColor(Color.WHITE);
        display_width = display_height = 0;
        _x = _y = 0;
        startX = startY = -1.0f;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (display_width > LENGTH_X && display_height > LENGTH_Y) {
            // _x, _y 재계산 (_width : _x = w : new _x)
            _x = _x * (float) w / display_width;
            _y = _y * (float) h / display_height;
        }
        else {
            _x = _y = 0;
        }

        display_width = w;
        display_height = h;

        if (display_width >= LENGTH_X && display_height >= LENGTH_Y) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas();
            mCanvas.setBitmap(mBitmap);

            mCanvas.drawRect((int) _x, (int) _y,
                    (int) _x + LENGTH_X, (int) _y + LENGTH_Y, drawPaint);
        }
        else {
            _x = _y = -1.0f;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBitmap, 0, 0, null);

        invalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float X = event.getX();
        float Y = event.getY();
        float next_x;
        float next_y;

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                if (startX >= 0) {
                }

                if (isInsideTheBox(X, Y)) {
                    startX = X;
                    startY = Y;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (startX < 0 || startY < 0) {
                    break;
                }

                next_x = _x + (X - startX);
                next_y = _y + (Y - startY);

                if (next_x < 0) next_x = 0.0f;
                if (next_x >= display_width - LENGTH_X) next_x = display_width - LENGTH_X;
                if (next_y < 0) next_y = 0.0f;
                if (next_y >= display_height - LENGTH_Y) next_y = display_height - LENGTH_Y;

                mCanvas.drawRect(_x, _y, _x + LENGTH_X,
                        _y + LENGTH_Y, erasePaint);

                mCanvas.drawRect(next_x, next_y,
                        next_x + LENGTH_X, next_y + LENGTH_Y,
                        drawPaint);
                invalidate(
                        _x < next_x? (int) _x : (int) next_x,
                        _y < next_y? (int) _y : (int) next_y,
                        _x < next_x? (int) (next_x + LENGTH_X) : (int) (_x + LENGTH_X),
                        _y < next_y? (int) (next_y + LENGTH_Y) : (int) (_y + LENGTH_Y)
                );

                startX = X;
                startY = Y;
                _x = next_x;
                _y = next_y;

                break;

            case MotionEvent.ACTION_UP:
                startX = -1.0f;
                startY = -1.0f;
                break;
        }

        return true;
    }

    boolean isInsideTheBox(float x, float y) {
        if (_x < 0 || _y < 0) {
            return false;
        }

        if ((x >= _x && x <= _x + LENGTH_X)
                && (y >= _y && y <= y + LENGTH_Y)) {
            return true;
        }
        return false;
    }
}