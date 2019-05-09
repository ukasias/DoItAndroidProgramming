package com.ukasias.android.doitmission13;

import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    final int howManyPannels = 2;
    TextView index;
    FrameLayout frameLayout;

    ArrayList<LinearLayout> frameList;

    Animation appearAnimation;
    Animation disapperAnimation;
    Animation appearDisappearAnimation;

    boolean justStarted = false;

    int currentIndex;
    Handler handler;
    boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        index = (TextView) findViewById(R.id.index);
        frameLayout = (FrameLayout) findViewById(R.id.frame);

        frameList = new ArrayList<LinearLayout>();

        makePannels(howManyPannels);

        appearDisappearAnimation = AnimationUtils.loadAnimation(
                this, R.anim.appear_disappear);

        currentIndex = -1;
        handler = new Handler();

        running = true;
        AnimationThread thread = new AnimationThread();
        thread.start();
    }

    public void makePannels(int howMany) {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);

        LinearLayout layout;

        for (int i = 0; i < howMany; i++) {
            layout = new LinearLayout(this);
            layout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            inflater.inflate(R.layout.customer_info, layout, true);
            makeInfoPage(layout, i);
            frameList.add(layout);
            frameLayout.addView(layout);
            Log.d("makePannels", "howMany: " + howMany);
            layout.setVisibility(View.INVISIBLE);
        }
    }

    public boolean makeInfoPage(LinearLayout frame, int index) {
        ImageView picture;
        TextView name;
        TextView phone;
        TextView address;

        int pictures[] = {R.drawable.customer, R.drawable.customer};
        String names[] = {"홍길동", "조완근"};
        String phones[] = {"010-7788-1234", "010-2962-6344"};
        String addresses[] = {"서울시", "동대문구 장안1동"};

        if (frame == null || (0 > index || index > 1)) {
            return false;
        }

        Resources res = getResources();
        picture = (ImageView) frame.findViewById(R.id.picture);
        picture.setImageDrawable(res.getDrawable(pictures[index]));

        name = (TextView) frame.findViewById(R.id.name);
        name.setText(names[index]);

        phone = (TextView) frame.findViewById(R.id.phone);
        phone.setText(phones[index]);

        address = (TextView) frame.findViewById(R.id.address);
        address.setText(addresses[index]);

        return true;
    }

    class AnimationThread extends Thread {
        int prevIndex;
        LinearLayout frame;

        @Override
        public void run() {
            while (running) {
                prevIndex = currentIndex;
                if (currentIndex < 0) {
                    prevIndex = currentIndex;
                    currentIndex = 0;
                } else {
                    currentIndex = nextIndex(currentIndex);
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        frame = frameList.get(currentIndex);
                        frame.setAnimation(appearDisappearAnimation);

                        if (prevIndex >= 0) {
                            frameList.get(prevIndex).setAnimation(null);
                        }
                        appearDisappearAnimation.start();
                        index.setText(String.valueOf(currentIndex + 1) + " / " +
                                String.valueOf(howManyPannels));
                    }
                });

                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e) {}
            }
        }
    }

    public int nextIndex(int index) {
        if ((index + 1) == howManyPannels) {
            return 0;
        } else {
            return (index + 1);
        }
    }
}