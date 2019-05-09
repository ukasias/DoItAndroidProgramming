package com.ukasias.android.graphics.custom.image;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
/*
        ImageView imgView = new ImageView(this);
        imgView.setImageResource(R.drawable.waterdrop);
        imgView.setAdjustViewBounds(true);

        mainLayout.addView(imgView, params);
        */
        CustomViewImage cvImage = new CustomViewImage(this);
        mainLayout.addView(cvImage, params);
    }
}
