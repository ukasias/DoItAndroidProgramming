package com.ukasias.android.thread.java;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

public class MainActivity extends AppCompatActivity {
    Button startButton;
    Button stopButton;
    ImageSwitcher switcher;
    Handler handler;
    boolean running;
    ImageThread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.start);
        stopButton = (Button) findViewById(R.id.stop);
        switcher = (ImageSwitcher) findViewById(R.id.switcher);
        handler = new Handler();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAnimation();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAnimation();
            }
        });

        switcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView view = new ImageView(getApplicationContext());
                view.setBackgroundColor(0xFF000000);
                view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                view.setLayoutParams(new ImageSwitcher.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                return view;
            }
        });
    }

    public void startAnimation() {
        switcher.setVisibility(View.VISIBLE);

        thread = new ImageThread();
        thread.start();
    }

    public void stopAnimation() {
        running = false;

        try {
            thread.join();
        } catch(InterruptedException e) {}

        switcher.setVisibility(View.INVISIBLE);
    }

    class ImageThread extends Thread {
        int duration = 250;
        final int imageId[] = { R.drawable.emo_im_surprised,
                                R.drawable.emo_im_sad,
                                R.drawable.emo_im_laughing,
                                R.drawable.emo_im_happy,
                                R.drawable.emo_im_crying };
        int currentIndex = 0;

        @Override
        public void run() {
            running = true;

            while(running) {
                synchronized (this) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            switcher.setImageResource(imageId[currentIndex]);
                        }
                    });
                }

                currentIndex++;
                if (currentIndex == imageId.length) {
                    currentIndex = 0;
                }

                try {
                    Thread.sleep(duration);
                } catch(InterruptedException e) {}
            }
        }
    }
}