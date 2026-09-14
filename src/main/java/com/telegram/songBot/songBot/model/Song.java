package com.telegram.songBot.songBot.model;

public class Song {
    private String index;
    private String title;
    private String uploader;
    private String url;

    public Song(String index, String title, String uploader, String url) {
        this.index = index;
        this.title = title;
        this.uploader = uploader;
        this.url = url;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return index + ". " + title + " - " + uploader + " [" + url + "]";
    }
}
