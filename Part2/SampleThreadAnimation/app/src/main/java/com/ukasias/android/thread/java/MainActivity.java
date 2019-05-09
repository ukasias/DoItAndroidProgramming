package com.ukasias.android.thread.java;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Handler handler;
    ArrayList<Drawable> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        handler = new Handler();
        images = new ArrayList<Drawable>();

        startAnimation();
    }

    public void startAnimation() {
        Resources res = getResources();
        images.add(res.getDrawable(R.drawable.emo_im_crying));
        images.add(res.getDrawable(R.drawable.emo_im_happy));
        images.add(res.getDrawable(R.drawable.emo_im_laughing));
        images.add(res.getDrawable(R.drawable.emo_im_sad));
        images.add(res.getDrawable(R.drawable.emo_im_surprised));

        AnimThread thread = new AnimThread();
        thread.start();
    }

    class AnimThread extends Thread {
        @Override
        public void run() {
            int index = 0;
            int size = images.size();
            for (int i = 0; i < 100; i++) {
                final Drawable drawable = images.get(index);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageDrawable(drawable);
                    }
                });

                if (++index == size) {
                    index = 0;
                }

                try {
                    Thread.sleep(500);
                } catch(InterruptedException e) {}
            }
        }
    }
}
