package com.ukasias.android.doitmission19;

import android.graphics.drawable.Drawable;
import android.net.Uri;

public class PictureItem {
    Uri thumbnail;
    String name;
    String date;

    public PictureItem(Uri thumbnail, String name, String date) {
        this.thumbnail = thumbnail;
        this.name = name;
        this.date = date;
    }

    public Uri getThumbnail() {
        return thumbnail;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public void setThumbnail(Uri thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
