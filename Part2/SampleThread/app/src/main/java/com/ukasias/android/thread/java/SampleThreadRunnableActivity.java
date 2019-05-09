package com.ukasias.android.thread.java;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SampleThreadRunnableActivity extends AppCompatActivity {
    TextView statusText;
    ProgressBar bar;

    Handler handler;
    ProgressRunnable runnable;

    boolean isRunning = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = (TextView) findViewById(R.id.statusText);
        bar = (ProgressBar) findViewById(R.id.progress);

        handler = new Handler();
        runnable = new ProgressRunnable();

        setTitle("SampleThreadRunnableActivity");
    }

    @Override
    protected void onStart() {
        super.onStart();

        bar.setProgress(0);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 20 && isRunning; i++) {
                        Thread.sleep(1000);

                        handler.post(runnable);
                    }
                }
                catch(Exception e) {
                    Log.e("StRunnableActivity",
                            "Exception in processing message", e);
                }
            }
        });
        isRunning = true;
        thread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        isRunning = false;
    }

    public class ProgressRunnable implements Runnable {
        @Override
        public void run() {
            bar.incrementProgressBy(5);

            if (bar.getProgress() == bar.getMax()) {
                statusText.setText("Done.");
            }
            else {
                statusText.setText("Runnable Working ... " + bar.getProgress());
            }
        }
    }
}
