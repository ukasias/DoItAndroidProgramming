package com.ukasias.android.sampleevent;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class OnTouchListenerImplementor implements View.OnTouchListener {
    public static final String TAG = "OnTouchLImplementor";

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();

        float curX = motionEvent.getX();
        float curY = motionEvent.getY();

        if (action == MotionEvent.ACTION_DOWN) {
            Log.i(TAG, "손가락 눌림 : " + curX + ", " + curY);
        }
        else if (action == MotionEvent.ACTION_MOVE) {
            Log.i(TAG, "손가락 움직임 : " + curX + ", " + curY);
        }
        else if (action == MotionEvent.ACTION_UP) {
            Log.i(TAG, "손가락 뗌 : " + curX + ", " + curY);
        }
        return true;
    }
}
