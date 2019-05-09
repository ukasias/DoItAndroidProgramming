package com.ukasias.android.doitmission18;

import android.graphics.drawable.Drawable;

public class BookItem {
    Drawable image;
    String title;
    String author;
    String contents;

    public BookItem(Drawable image, String title, String author, String contents) {
        this.image = image;
        this.title = title;
        this.author = author;
        this.contents = contents;
    }

    public Drawable getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getContents() {
        return contents;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
