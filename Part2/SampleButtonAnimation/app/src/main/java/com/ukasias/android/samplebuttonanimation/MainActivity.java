package com.ukasias.android.samplebuttonanimation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    Animation flowAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);

        flowAnim = AnimationUtils.loadAnimation(this,
                R.anim.flow);
        flowAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Toast.makeText(getApplicationContext(),
                        "애니메이션 종료됨",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void onButton1Clicked(View v) {
        // <1> 바로 실행.
        textView.startAnimation(flowAnim);

        /* <2> 세팅했다 나중에 실행  => 동작 안함
        textView.setAnimation(flowAnim);
        flowAnim.startNow(); */
    }
}
