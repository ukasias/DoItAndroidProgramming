package com.ukasias.android.push;

import android.graphics.drawable.Drawable;

public class FriendItem {
    Drawable picture;
    String name;
    String phone;

    FriendItem(Drawable picture, String name, String phone) {
        this.picture = picture;
        this.name = name;
        this.phone = phone;
    }

    public Drawable getPicture() {
        return picture;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPicture(Drawable picture) {
        this.picture = picture;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
