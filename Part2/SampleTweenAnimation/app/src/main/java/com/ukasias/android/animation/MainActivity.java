package com.ukasias.android.animation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button scaleButton;
    Button scaleButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scaleButton = (Button) findViewById(R.id.scaleButton);
        scaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation anim =
                        AnimationUtils.loadAnimation(
                                getApplicationContext(),
                                R.anim.scale);
                view.startAnimation(anim);
            }
        });

        scaleButton2 = (Button) findViewById(R.id.scaleButton2);
        scaleButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation anim =
                        AnimationUtils.loadAnimation(
                                getApplicationContext(),
                                R.anim.scale2);
                view.startAnimation(anim);
            }
        });
    }
}
