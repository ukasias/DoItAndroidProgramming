package com.ukasias.android.push;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PopUpActivity extends AppCompatActivity {
    final static String TAG = "PopUpActivity";

    Button checkButton;
    TextView timeView;
    TextView contentsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);

        timeView = this.findViewById(R.id.time);
        contentsView = this.findViewById(R.id.contents);
        checkButton = this.findViewById(R.id.checkButton);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        print("onCreate() called.");

        init(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        print("onNewIntent() called.");
        init(intent);

        super.onNewIntent(intent);
    }

    private void init(Intent intent) {
        Bundle bundle = intent.getExtras();

        timeView.setText(bundle.get("date").toString());
        contentsView.setText("From: " + bundle.get("from").toString() + "\n");
        contentsView.append(bundle.get("contents").toString());

        print("from : " + bundle.get("from"));
        print("date : " + bundle.get("date"));
        print("contents : " + bundle.get("contents"));
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }
}