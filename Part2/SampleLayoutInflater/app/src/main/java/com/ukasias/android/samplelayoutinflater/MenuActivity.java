package com.ukasias.android.samplelayoutinflater;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class MenuActivity extends AppCompatActivity {
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        container = (LinearLayout) findViewById(R.id.container);

        Button button = (Button) findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater;


                // <1> getSystemService() 이용
                /*
                inflater = (LayoutInflater)
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                inflater.inflate(R.layout.sub1, container, true);
                */

                // <2> LayoutInflater 클래스의 from() 메소드 이용
                /*
                inflater = (LayoutInflater) LayoutInflater.from(getApplicationContext());
                inflater.inflate(R.layout.sub1, container, true);
                */

                // <3> static inflate() 함수로 바로 inflation.
                View.inflate(getApplicationContext(), R.layout.sub1, container);

                CheckBox checkBox = (CheckBox) container.findViewById(R.id.checkBox);
                checkBox.setText("로딩되었어요.");
            }
        });
    }
}
