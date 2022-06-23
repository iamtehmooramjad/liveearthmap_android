package com.dev175.liveearthmap.model;

public class Compass {
    int image;
    String type;

    public Compass() {
    }

    public Compass(int image, String type) {
        this.image = image;
        this.type = type;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

