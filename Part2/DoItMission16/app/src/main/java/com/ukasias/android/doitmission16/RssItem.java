package com.ukasias.android.doitmission16;

import android.graphics.drawable.Drawable;

public class RssItem {
    Drawable icon;
    String title;
    String contents;
    String link;

    public RssItem(Drawable icon, String title, String contents, String link) {
        this.icon = icon;
        this.title = title;
        this.contents = contents;
        this.link = link;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
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

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
