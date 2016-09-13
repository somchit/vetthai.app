package com.vetthai.app.model;

/**
 * Created by th.panya.bas on 10/15/2015.
 */
public class MenuUrl {

    private String title;
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public MenuUrl(String title, String url) {
        this.title = title;
        this.url = url;
    }
}
