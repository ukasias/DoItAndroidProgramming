package com.ukasias.android.samplecalendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    Button preButton;
    Button nextButton;
    TextView textView;
    GridView gridView;
    MonthAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preButton = (Button) findViewById(R.id.monthPrevious);
        nextButton = (Button) findViewById(R.id.monthNext);
        textView = (TextView) findViewById(R.id.monthText);
        gridView = (GridView) findViewById(R.id.monthView);

        adapter = new MonthAdapter(this);
        gridView.setAdapter(adapter);


        preButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.setPrevMonth();
                adapter.notifyDataSetChanged();
                setTextView();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.setNextMonth();
                adapter.notifyDataSetChanged();
                setTextView();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d("MainActivity", "onItemClicked(): " + position);

            }
        });



        setTextView();
    }

    public void setTextView() {
        textView.setText(String.valueOf(adapter.getYear() + "년 " +
                        String.valueOf(adapter.getMonth() + 1) + "월"));
    }
}
