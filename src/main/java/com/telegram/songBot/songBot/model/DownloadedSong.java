package com.telegram.songBot.songBot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "downloaded_songs")
public class DownloadedSong {

    @Id
    private String songId;
    private String title;
    private String uploader;

    @jakarta.persistence.Column(length = 1000)
    private String songUrl;

    public DownloadedSong() {}

    public DownloadedSong(String songId, String title, String uploader, String songUrl) {
        this.songId = songId;
        this.title = title;
        this.uploader = uploader;
        this.songUrl = songUrl;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
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

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }
}
