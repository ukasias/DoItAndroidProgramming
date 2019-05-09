package com.ukasias.android.sampleevent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // View 1 --------------------------------------------------------
        textView = (TextView) findViewById(R.id.textView);

        View view = (View) findViewById(R.id.view);

        /* 별도의 OnTouchListener interface를 구현한 class의 객체를 지정하는 경우.
        OnTouchListenerImplementor implementor = new OnTouchListenerImplementor();
        view.setOnTouchListener((View.OnTouchListener) implementor);
        */


        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();

                float curX = motionEvent.getX();
                float curY = motionEvent.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    println("손가락 눌림 : " + curX + ", " + curY);
                }
                else if (action == MotionEvent.ACTION_MOVE) {
                    println("손가락 움직임 : " + curX + ", " + curY);
                }
                else if (action == MotionEvent.ACTION_UP) {
                    println("손가락 뗌 : " + curX + ", " + curY);
                }
                return true;
            }
        });

        // View 2 --------------------------------------------------------

        detector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                println("onDown() 호출됨");
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {
                println("onShowPress() 호출됨");
            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                println("onSingleTapUp() 호출됨");
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                println("onScroll() 호출됨" + v + ", " + v1);
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {
                println("onLongPress() 호출됨");
            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                println("onFling() 호출됨: " + v + ", " + v1);
                return false;
            }
        });

        View view2 = (View) findViewById(R.id.view2);
        view2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                detector.onTouchEvent(motionEvent);
                return true;
            }
        });

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText("");
            }
        });
    }

    public void println(String data) {
        textView.append(data + "\n");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 시스템 [BACK] 버튼이 눌렸을 경우, 토스트 메시지 보여주기
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(getApplicationContext(),
                    "시스템 [BACK] 버튼이 눌렸습니다.",
                    Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }
}
