package com.ukasias.android.sampleservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText =  findViewById(R.id.editText);

        Intent passedIntent = getIntent();
        processIntent(passedIntent);
    }

    public void onButton1Clicked(View v) {
        if (editText.getText().toString().length() > 0) {
            Intent intent = new Intent(getApplicationContext(), MyService.class);
            intent.putExtra("command", "show");
            intent.putExtra("name", editText.getText().toString());

            startService(intent);
        }
        else {
            Toast.makeText(getApplicationContext(),
                    "이름을 입력하세요",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        processIntent(intent);

        super.onNewIntent(intent);
    }

    private void processIntent(Intent intent) {
        if (intent != null) {
            String command = intent.getStringExtra("command");
            String name = intent.getStringExtra("name");

            Toast.makeText(getApplicationContext(),
                    "command: " + command + ", name: " + name,
                    Toast.LENGTH_LONG).show();
        }
    }
}
