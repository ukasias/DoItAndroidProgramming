package com.ukasias.android.doitmission20;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "DoItMission20";

    FrameLayout frameLayout;

    ImageView coupon1;
    TextView coupon1Text;

    ImageView coupon2;
    TextView coupon2Text;

    Button showButton;
    Button hideButton;

    CameraSurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = findViewById(R.id.frameLayout);

        coupon1 = findViewById(R.id.coupon1);
        coupon1Text = findViewById(R.id.vips);

        coupon2 = findViewById(R.id.coupon2);
        coupon2Text = findViewById(R.id.starbucks);

        showButton = findViewById(R.id.showButton);
        hideButton = findViewById(R.id.hideButton);

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCoupons(true);
            }
        });
        hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCoupons(false);
            }
        });

        startCameraView();
    }

    private void startCameraView() {
        surfaceView = new CameraSurfaceView(this);
        frameLayout.addView(surfaceView);
    }

    private void setCoupons(boolean show) {
        int value = show? View.VISIBLE : View.INVISIBLE;

        coupon1.setVisibility(value);
        coupon1Text.setVisibility(value);
        coupon2.setVisibility(value);
        coupon2Text.setVisibility(value);
    }
}
