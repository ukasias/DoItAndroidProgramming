package com.ukasias.android.samplecalendar;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

/*
    일자에 표시하는 텍스트 뷰 정의.
    TextView 하나만 들어가므로 AppCompatTextView를 상속
 */

public class MonthItemView extends AppCompatTextView {
    private MonthItem item;

    public MonthItemView(Context context) {
        super(context);
        init();
    }

    public MonthItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        setBackgroundColor(Color.WHITE);
    }

    public MonthItem getItem() {
        return item;
    }

    public void setItem(MonthItem item) {
        this.item = item;

        int day = item.getDay();
        if (day != 0) {
            setText(String.valueOf(day));
        }
        else {
            setText("");
        }
    }
}
