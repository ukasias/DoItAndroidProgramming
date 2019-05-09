package com.ukasias.android.doitmission_03;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    ImageView viewUp;
    ImageView viewDown;
    Button buttonUp;
    Button buttonDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewUp = (ImageView) findViewById(R.id.imageView);
        //viewUp.setVisibility(View.VISIBLE);
        viewDown = (ImageView) findViewById(R.id.imageView2);
        //viewDown.setVisibility(View.INVISIBLE);
        buttonUp = (Button) findViewById(R.id.button);
        buttonDown = (Button) findViewById(R.id.button2);

        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeToUp();
            }
        });

        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeToDown();
            }
        });
    }

    // These will not be called.
    public void onButtonUpClicked(View v) {
        viewUp.setVisibility(View.VISIBLE);
        viewDown.setVisibility(View.INVISIBLE);
    }

    public void onButtonDownClicked(View v) {
        viewUp.setVisibility(View.INVISIBLE);
        viewDown.setVisibility(View.VISIBLE);
    }

    public void changeToUp() {
        viewUp.setImageResource(R.drawable.beach);
        viewDown.setImageResource(0);

        viewUp.invalidate();
        viewDown.invalidate();
    }

    public void changeToDown() {
        viewUp.setImageResource(0);
        viewDown.setImageResource(R.drawable.beach);

        viewUp.invalidate();
        viewDown.invalidate();
    }
}
