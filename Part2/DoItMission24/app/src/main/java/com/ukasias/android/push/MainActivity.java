package com.ukasias.android.push;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "MainActivity";

    Button popupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.startService(
                new Intent(this, MyFirebaseMessagingService.class));
        finish();
        /*
        setContentView(R.layout.activity_main);

        popupButton = findViewById(R.id.popupButton);
        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), com.ukasias.android.push.PopUpActivity.class);
                startActivity(intent);
            }
        });

        toast("ID is \n" + FirebaseInstanceId.getInstance().getToken().toString());

        print(FirebaseInstanceId.getInstance().getToken().toString());
        */
    }

    private void toast(String message) {
        Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_LONG).show();
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }
}