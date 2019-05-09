package com.ukasias.android.samplerssfeeder;

import android.graphics.drawable.Drawable;

public class RSSNewsItem {
    Drawable icon;
    String title;
    String date;
    String contents;
    String link;

    public RSSNewsItem() {
    }

    public RSSNewsItem(Drawable icon, String title, String date, String contents, String link) {
        this.icon = icon;
        this.title = title;
        this.date = date;
        this.contents = contents;
        this.link = link;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getContents() {
        return contents;
    }

    public String getLink() {
        return link;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
