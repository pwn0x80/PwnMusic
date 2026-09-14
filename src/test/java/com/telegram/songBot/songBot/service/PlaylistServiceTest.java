package com.telegram.songBot.songBot.service;

import com.telegram.songBot.songBot.client.YtDlpClient;
import com.telegram.songBot.songBot.model.Song;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class PlaylistServiceTest {

    private YtDlpClient ytDlpClient;
    private KafkaTemplate<String, String> kafkaTemplate;
    private PlaylistService playlistService;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        ytDlpClient = Mockito.mock(YtDlpClient.class);
        kafkaTemplate = Mockito.mock(KafkaTemplate.class);
        playlistService = new YtDlpPlaylistServiceImpl(ytDlpClient, kafkaTemplate);
    }

    @Test
    void testFetchPlaylistSongsAndStoreInKafka() throws Exception {
        when(ytDlpClient.executeProcess(anyList()))
                .thenReturn(List.of("1||Song One||Artist One||video123||https://www.jiosaavn.com/song/video123"));

        List<Song> songs = playlistService.fetchPlaylistSongs();

        assertEquals(1, songs.size());
        assertEquals("1", songs.get(0).getIndex());
        assertEquals("Song One", songs.get(0).getTitle());
        assertEquals("Artist One", songs.get(0).getUploader());
        assertEquals("https://www.jiosaavn.com/song/video123", songs.get(0).getUrl());

        Mockito.verify(kafkaTemplate).send(eq("songTopic"), eq("video123"), eq("{\"key\":\"video123\",\"name\":\"Song One\",\"url\":\"https://www.jiosaavn.com/song/video123\",\"id\":\"video123\"}"));
    }
}
