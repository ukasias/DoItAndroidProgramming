package com.ukasias.android.thread.java;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    ProgressBar bar;
    Button executeButton;
    Button cancelButton;

    BackgroundTask task;
    int value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        bar = (ProgressBar) findViewById(R.id.progress);
        executeButton = (Button) findViewById(R.id.executeButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        executeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task = new BackgroundTask();
                task.execute(100);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (task != null) {
                    task.cancel(true);
                }
            }
        });
    }

    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            value = 0;
            bar.setProgress(value);
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            while (isCancelled() == false) {
                value++;
                if (value >= 100) {
                    break;
                }
                else {
                    publishProgress(value);
                }

                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {}
            }
            return value;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            bar.setProgress(values[0].intValue());
            textView.setText("Current Value: " + values[0].toString());
        }

        @Override
        protected void onPostExecute(Integer integer) {
            bar.setProgress(0);
            textView.setText("Finished.");
        }

        @Override
        protected void onCancelled() {
            bar.setProgress(0);
            textView.setText("Canceled.");
        }
    }
}
