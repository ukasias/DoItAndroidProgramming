package com.ukasias.android.doitmission_06;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_MENU = 101;
    EditText idText;
    EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idText = (EditText) findViewById(R.id.id);
        passwordText = (EditText) findViewById(R.id.password);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idText.getText().toString().length() > 0 &&
                        passwordText.getText().toString().length() > 0) {

                    Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                    intent.putExtra("name", getString(R.string.main));
                    startActivity(intent);
                    finish();

                }
                else {
                    Toast.makeText(getApplicationContext(),
                            R.string.inputError, Toast.LENGTH_LONG).show();
                }
            }
        });

        Intent passedIntent = getIntent();
        processIntent(passedIntent);
    }

    private void processIntent(Intent intent) {
        String from;
        if ((from = intent.getStringExtra("name")) != null) {
            Toast.makeText(getApplicationContext(),
                    "From : " + from, Toast.LENGTH_LONG).show();
        }
    }
}
