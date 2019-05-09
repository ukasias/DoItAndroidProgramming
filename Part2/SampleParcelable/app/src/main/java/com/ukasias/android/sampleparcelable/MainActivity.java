package com.ukasias.android.sampleparcelable;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_MENU = 101;
    public static final String KEY_SIMPLE_DATA = "data";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);

        SimpleData data = new SimpleData(100, "Hello Android!");
        intent.putExtra(KEY_SIMPLE_DATA, data);

        startActivityForResult(intent, REQUEST_CODE_MENU);
    }
}
