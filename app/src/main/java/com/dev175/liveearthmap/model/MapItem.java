package com.dev175.liveearthmap.model;

public class MapItem {
    private String title;
    private int icon;

    public MapItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public MapItem() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
