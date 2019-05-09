package com.ukasias.android.sampleanimation;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ImageView sky;
    ImageView swing;
    ImageView water;

    Animation flowAnimation;
    Animation shakeAnimation;
    Animation dropAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sky = (ImageView) findViewById(R.id.skyImage);
        swing = (ImageView) findViewById(R.id.swingImage);
        water = (ImageView) findViewById(R.id.waterImage);

        flowAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.flow);
        shakeAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.shake);
        dropAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.drop);

        sky.setAnimation(flowAnimation);
        swing.setAnimation(shakeAnimation);
        water.setAnimation(dropAnimation);

        Resources res = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.sky_background);

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        ViewGroup.LayoutParams params = sky.getLayoutParams();
        params.width = bitmapWidth;
        params.height = bitmapHeight;

        sky.setImageBitmap(bitmap);
        flowAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Toast.makeText(getApplicationContext(), "onWindowFocusChanged : " + hasFocus,
                Toast.LENGTH_LONG).show();

        if (hasFocus) {
            flowAnimation.start();
            shakeAnimation.start();
            dropAnimation.start();
        }
        else {
            flowAnimation.reset();
            shakeAnimation.reset();
            dropAnimation.reset();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
