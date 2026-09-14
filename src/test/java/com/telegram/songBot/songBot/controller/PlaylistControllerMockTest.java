package com.telegram.songBot.songBot.controller;

import io.github.cdimascio.dotenv.Dotenv;
import com.telegram.songBot.songBot.model.Song;
import com.telegram.songBot.songBot.service.PlaylistService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class PlaylistControllerMockTest {

    @BeforeAll
    static void loadEnv() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    @Autowired
    private PlaylistController playlistController;

    @MockitoBean
    private PlaylistService playlistService;

    @Test
    void testGetLatestSongs() {
        List<Song> mockSongs = Arrays.asList(
                new Song("1", "Mock Song", "Mock Artist", "https://www.jiosaavn.com/song/mock123")
        );
        when(playlistService.fetchPlaylistSongs()).thenReturn(mockSongs);

        List<Song> response = playlistController.getLatestSongs();
        assertEquals(1, response.size());
        assertEquals("Mock Song", response.get(0).getTitle());
        assertEquals("https://www.jiosaavn.com/song/mock123", response.get(0).getUrl());
    }
}
