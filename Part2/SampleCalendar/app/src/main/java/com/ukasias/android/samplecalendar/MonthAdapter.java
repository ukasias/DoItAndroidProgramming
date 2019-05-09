package com.ukasias.android.samplecalendar;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TableLayout;

import java.util.Calendar;

public class MonthAdapter extends BaseAdapter {
    public static final String TAG = "MonthAdapter";

    public static int oddColor = Color.rgb(225, 225, 225);
    public static int headColor = Color.rgb(12, 32, 158);

    private int selectedPosition;

    Context mContext;
    private MonthItem[] items;
    private Calendar calendar;

    private final int numColumns = 7;
    private final int maxWeeks = 6; // 31(최다 일) + 6(시작주일이 1일인 경우) / 7 = 5.x > 5.
    private final int maxGridCells = numColumns * maxWeeks;

    int emptyDaysOfFirstWeek;       // 첫주의 시작전 날들
    int daysOfMonth;    // 이번 달이 며칠로 이루어져 있나?
    int year;           // 년도
    int month;          // 달

    public MonthAdapter(Context context) {
        super();
        mContext = context;

        init();
    }

    public void init() {
        items = new MonthItem[maxGridCells];
        for (int position = 0; position < maxGridCells; position++) {
            items[position] = new MonthItem();
        }

        calendar = Calendar.getInstance();
        selectedPosition = -1;

        recalculate();
        resetDayNumbers();
    }

    public void recalculate() {
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        emptyDaysOfFirstWeek = calendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
        daysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);

        Log.d(TAG, "빈날 수    : " + emptyDaysOfFirstWeek);
        Log.d(TAG, "이번 달 수 : " + daysOfMonth);
        Log.d(TAG, "년도       : " + year);
        Log.d(TAG, "달        : " + month);
    }

    public void resetDayNumbers() {
        int date = 0;

        for (int position = 0; position < maxGridCells; position++) {
            if (position < emptyDaysOfFirstWeek) {
                items[position].setDay(0);
            }
            else {
                if (++date <= daysOfMonth) {
                    items[position].setDay(date);
                } else {
                    items[position].setDay(0);
                }
            }
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        MonthItemView cellView;
        if (view == null) {
            cellView = new MonthItemView(mContext);
        }
        else {
            cellView = (MonthItemView) view;
        }

        GridView.LayoutParams params = new GridView.LayoutParams(
                GridView.LayoutParams.MATCH_PARENT,
                200);
        cellView.setLayoutParams(params);
        cellView.setPadding(2,20,2, 2);
        cellView.setItem(items[position]);

        if ((items[position].getDay() > 0) && (position % numColumns == 0)) {
            cellView.setTextColor(Color.RED);
        }
        else {
            cellView.setTextColor(Color.BLACK);
        }

        if ((items[position].getDay() > 0) && (position == selectedPosition)) {
            cellView.setBackgroundColor(Color.YELLOW);
        }
        else {
            cellView.setBackgroundColor(Color.WHITE);
        }

        return cellView;
    }

    public void setPrevMonth() {
        calendar.add(Calendar.MONTH, -1);
        recalculate();
        resetDayNumbers();
    }

    public void setNextMonth() {
        calendar.add(Calendar.MONTH, 1);
        recalculate();
        resetDayNumbers();
    }

    public void onItemClicked(int position) {
        if (items[position].getDay() > 0) {
            if (selectedPosition < 0) {
                selectedPosition = position;
            }
            else {
                selectedPosition = -1;
            }
        }
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return items[i];
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }
}
