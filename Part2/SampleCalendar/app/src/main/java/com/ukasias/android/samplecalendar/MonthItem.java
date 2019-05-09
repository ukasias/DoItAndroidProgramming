package com.ukasias.android.samplecalendar;

/*
    일자 정보를 담기 위한 클래스 정의: 딱 day 값만 저장, 읽기
 */
public class MonthItem {
    private int day;

    public MonthItem() {

    }

    public MonthItem(int day) {
        this.day = day;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
