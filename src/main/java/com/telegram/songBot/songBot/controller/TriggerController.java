package com.telegram.songBot.songBot.controller;

import com.telegram.songBot.songBot.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TriggerController {

    private final PlaylistService playlistService;

    @Autowired
    public TriggerController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping("/trigger-download")
    public ResponseEntity<String> triggerDownload(@RequestParam(required = false, defaultValue = "secret") String key) {
        playlistService.fetchPlaylistSongs();
        return ResponseEntity.ok("Playlist fetched and songs published to Kafka successfully!");
    }
}
