package com.ukasias.android.doitmission10;

public class ProductItem {
    int image;
    String name;
    int price;
    String comment;

    public ProductItem(int image, String name, int price, String comment) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.comment = comment;
    }

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getComment() {
        return comment;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
