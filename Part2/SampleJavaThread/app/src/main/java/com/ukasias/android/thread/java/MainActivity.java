package com.ukasias.android.thread.java;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private int value = 0;
    private boolean running = false;

    Button showButton;
    TextView valueText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showButton = (Button) findViewById(R.id.showButton);
        valueText = (TextView) findViewById(R.id.valueText);

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                valueText.setText("스레드에서 받은 값: " + value);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        running = true;

        BackgroundThread thread = new BackgroundThread();
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        running = false;
        value = 0;
    }

    class BackgroundThread extends Thread {
        public void run() {
            while(running) {
                try {
                    Thread.sleep(1000);
                    value++;
                }
                catch (InterruptedException e) {
                    Log.e("SampleJavaThread", "Exception in Thread", e);
                }
            }
        }
    }
}
