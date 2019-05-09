package com.ukasias.android.sampledatetimepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import java.util.Calendar;

public class DateTimePicker extends LinearLayout {

    public static interface onDateTimeChangedListener {
        void onDateTimeChanged(DateTimePicker view, int year, int monthOfYear, int dayOfYear,
                               int hourOfDay, int minute);
    }

    private DatePicker datePicker;
    private CheckBox enableTimeCheckBox;
    private TimePicker timePicker;
    private onDateTimeChangedListener listener;

    public DateTimePicker(Context context) {
        super(context);
        init(context);
    }

    public DateTimePicker(Context context, AttributeSet attr) {
        super(context, attr);
        init(context);
    }

    public void init(Context context) {
        // XML layout inflation
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.picker, this, true);

        //시간 정보 얻어오기
        Calendar calendar = Calendar.getInstance();
        final int curYear = calendar.get(Calendar.YEAR);
        final int curMonth = calendar.get(Calendar.MONTH);
        final int curDay = calendar.get(Calendar.DAY_OF_MONTH);
        final int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        final int curMin = calendar.get(Calendar.MINUTE);

        // 날짜 선택 위젯 초기화
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        datePicker.init(curYear, curMonth, curDay, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                if (listener != null) {
                    listener.onDateTimeChanged(DateTimePicker.this,
                            year, monthOfYear, dayOfMonth,
                            timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                }
            }
        });

        // 체크 박스 이벤트 처리
        enableTimeCheckBox = (CheckBox) findViewById(R.id.enableTimeCheckBox);
        enableTimeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                timePicker.setEnabled(isChecked);
                timePicker.setVisibility(enableTimeCheckBox.isChecked()?
                                                    View.VISIBLE : View.INVISIBLE);
            }
        });

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setCurrentHour(curHour);
        timePicker.setCurrentMinute(curMin);
        timePicker.setEnabled(enableTimeCheckBox.isChecked());
        timePicker.setVisibility(enableTimeCheckBox.isChecked()?
                                    View.VISIBLE : View.INVISIBLE);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                if (listener != null) {
                    listener.onDateTimeChanged(DateTimePicker.this,
                            datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                            hour, minute);
                }
            }
        });
    }

    public void setOnDateTimeChangedListener(onDateTimeChangedListener dateTimeListener) {
        listener = dateTimeListener;
    }

    public void updateDateTime(int year, int monthOfYear, int dayOfMonth,
                               int currentHour, int currentMinute) {
        datePicker.updateDate(year, monthOfYear, dayOfMonth);
        timePicker.setCurrentHour(currentHour);
        timePicker.setCurrentMinute(currentMinute);
    }

    public int getYear() {
        return datePicker.getYear();
    }

    public int getMonth() {
        return datePicker.getMonth();
    }

    public int getDayOfMonth() {
        return datePicker.getDayOfMonth();
    }

    public int getCurrentHour() {
        return timePicker.getCurrentHour();
    }

    public int getCurrentMinute() {
        return timePicker.getCurrentMinute();
    }
}
