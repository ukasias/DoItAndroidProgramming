package com.ukasias.android.doitmission_09;

import android.widget.TextView;

public class CustomerInfoItem {
    private int icon;
    private String name;
    private String birth;
    private String phoneNumber;

    public CustomerInfoItem(String name, String birth, String phoneNumber) {
        this.icon = R.drawable.customer;
        this.name = name;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
    }

    public CustomerInfoItem(int icon, String name, String birth, String phoneNumber) {
        this.icon = icon;
        this.name = name;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
    }
    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getBirth() {
        return birth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
