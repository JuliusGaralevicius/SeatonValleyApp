package com.seatonvalleyccapp.News;

import android.graphics.drawable.Drawable;

/**
 * Created by V on 26/03/2018.
 */

public class NewsItem {
    private String title;
    private String shortDescription;
    private Drawable thumbnail;
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Drawable getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Drawable thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public NewsItem(String title, String shortDescription, Drawable thumbnail, String url) {

        this.title = title;
        this.shortDescription = shortDescription;
        this.thumbnail = thumbnail;
        this.url = url;
    }
}
