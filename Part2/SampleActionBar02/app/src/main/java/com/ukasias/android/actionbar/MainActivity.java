package com.ukasias.android.actionbar;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ActionBar aBar;
    EditText editText;
    TextView.OnEditorActionListener onSearchListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aBar = getSupportActionBar();
        onSearchListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Toast.makeText(getApplicationContext(),
                        "검색어: " + textView.getText().toString(),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        View v = menu.findItem(R.id.menu_search).getActionView();

        if (v != null) {
            editText = (EditText) v.findViewById(R.id.editText);

            if (editText != null) {
                editText.setOnEditorActionListener(onSearchListener);
            }
        }
        return true;
    }
}
