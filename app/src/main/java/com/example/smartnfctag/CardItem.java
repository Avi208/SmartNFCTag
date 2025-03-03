package com.example.smartnfctag;

public class CardItem {
    private String title;
    private int imageResId;

    public CardItem(String title, int imageResId) {
        this.title = title;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public int getImageResId() {
        return imageResId;
    }
}