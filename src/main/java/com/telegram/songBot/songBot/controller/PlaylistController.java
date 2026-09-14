package com.telegram.songBot.songBot.controller;

import com.telegram.songBot.songBot.model.Song;
import com.telegram.songBot.songBot.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PlaylistController {

    private final PlaylistService playlistService;

    @Autowired
    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping("/latest-song")
    public List<Song> getLatestSongs() {
        List<Song> songs = playlistService.fetchPlaylistSongs();
        System.out.println("Fetched Songs Array:");
        for (Song song : songs) {
            System.out.println(song);
        }
        return songs;
    }
}

