package com.ukasias.android.samplegraphanimation;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    LinearLayout layout;
    Resources res;
    Animation growAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = (LinearLayout) findViewById(R.id.mainLayout);
        res = getResources();

        growAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.grow);

        addItem("Apple", 80);
        addItem("Orange", 100);
        addItem("kiwi", 40);
        addItem("mango", 35);
        addItem("watermelon", 90);
    }

    private void addItem(String name, int value) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView textview = new TextView(this);
        textview.setText(name);
        params.width = 250;
        params.setMargins(0, 4, 0, 4);
        itemLayout.addView(textview, params);

        ProgressBar bar = new ProgressBar(this, null,
                android.R.attr.progressBarStyleHorizontal);
        bar.setIndeterminate(false);
        bar.setMax(100);
        bar.setProgress(100);
        bar.setAnimation(growAnimation);
        params2.height = 80;
        params2.width = value * 5;
        params2.gravity = Gravity.LEFT;

        itemLayout.addView(bar, params2);

        params3.setMargins(10, 10, 10, 10);

        layout.addView(itemLayout, params3);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Toast.makeText(getApplicationContext(),
                "onWindowFocusChanged: " + hasFocus,
                Toast.LENGTH_LONG).show();

        if (hasFocus) {
            growAnimation.start();
        } else {
            growAnimation.reset();
        }
    }
}
