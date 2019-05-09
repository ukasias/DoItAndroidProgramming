package com.ukasias.android.sampledatetimepicker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    DateTimePicker dateTimePicker;
    final SimpleDateFormat sdFormat =
            new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        dateTimePicker = (DateTimePicker) findViewById(R.id.dateTimePicker);

        // Text 초기화
        Calendar calendar = Calendar.getInstance();
        calendar.set(dateTimePicker.getYear(), dateTimePicker.getMonth(), dateTimePicker.getDayOfMonth(),
                dateTimePicker.getCurrentHour(), dateTimePicker.getCurrentMinute());
        textView.setText(sdFormat.format(calendar.getTime()));

        // Listener 정의
        dateTimePicker.setOnDateTimeChangedListener(new DateTimePicker.onDateTimeChangedListener() {
            @Override
            public void onDateTimeChanged(DateTimePicker view,
                                          int year, int monthOfYear, int dayOfYear,
                                          int hourOfDay, int minute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfYear, hourOfDay, minute);
                textView.setText(sdFormat.format(calendar.getTime()));
            }
        });
    }
}
