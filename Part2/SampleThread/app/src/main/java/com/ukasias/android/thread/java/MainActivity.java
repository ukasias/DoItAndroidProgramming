package com.ukasias.android.thread.java;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    TextView statusText;
    ProgressBar progressBar;

    boolean isRunning = false;

    ProgressHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = (TextView) findViewById(R.id.statusText);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        handler = new ProgressHandler();
    }

    @Override
    protected void onStart() {
        super.onStart();

        progressBar.setProgress(0);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 20; i++) {
                        Thread.sleep(1000);

                        Message msg = handler.obtainMessage();
                        Bundle bd = new Bundle();
                        bd.putInt("hello", 10);
                        msg.setData(bd);
                        handler.sendMessage(msg);
                    }
                } catch(Exception e) {
                    Log.e("MainActivity", "Exception in processing message",
                            e);
                }
            }
        });

        isRunning = true;
        thread.start();

        Message msg = handler.obtainMessage();
        Bundle bd = new Bundle();
        bd.putInt("hello", 20);
        msg.setData(bd);
        handler.sendMessage(msg);
    }

    @Override
    protected void onStop() {
        super.onStop();

        isRunning = false;
    }

    public class ProgressHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            progressBar.incrementProgressBy(5);
            int msgInt = msg.getData().getInt("hello");
            Log.d("ProgressHandler", "hello: " + msgInt);
            if (progressBar.getProgress() == progressBar.getMax()) {
                statusText.setText("Done");
            }
            else {
                statusText.setText("Workding ... " + progressBar.getProgress());
            }
        }
    }
}
