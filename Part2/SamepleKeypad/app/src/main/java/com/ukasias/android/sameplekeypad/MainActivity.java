package com.ukasias.android.sameplekeypad;

import android.content.Context;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    InputMethodManager manager;
    Button button;
    Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.editText).requestFocus();
                manager.showSoftInput((View) findViewById(R.id.editText),
                        InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }
}
