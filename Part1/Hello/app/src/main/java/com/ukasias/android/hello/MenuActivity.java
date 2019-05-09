package com.ukasias.android.hello;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void onBackButtonClicked(View v) {
        /*
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.daum.net"));
        startActivity(myIntent);
        */
        Toast.makeText(getApplicationContext(), "돌아가기 버튼을 눌렀어요", Toast.LENGTH_LONG).show();
        finish();
    }
}
