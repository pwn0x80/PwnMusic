package com.telegram.songBot.songBot.service;

import com.telegram.songBot.songBot.client.YtDlpClient;
import com.telegram.songBot.songBot.model.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class YtDlpPlaylistServiceImpl implements PlaylistService {

    private static final String TOPIC_NAME = "songTopic";
    private final YtDlpClient ytDlpClient;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public YtDlpPlaylistServiceImpl(YtDlpClient ytDlpClient, KafkaTemplate<String, String> kafkaTemplate) {
        this.ytDlpClient = ytDlpClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public List<Song> fetchPlaylistSongs() {
        List<Song> songs = new ArrayList<>();
        String playlistUrl = "https://www.jiosaavn.com/featured/chartbusters-2026-international/ck-nuL,boLnuS6o99JG3mQ__";

        try {
            List<String> outputLines = ytDlpClient.executeProcess(List.of(
                    "yt-dlp",
                    "--flat-playlist",
                    "--print",
                    "%(playlist_index)s||%(title)s||%(uploader)s||%(id)s||%(url)s",
                    playlistUrl
            ));

            for (int i = 0; i < outputLines.size(); i++) {
                String line = outputLines.get(i);
                String[] parts = line.split("\\|\\|", -1);

                String index = parts.length > 0 ? parts[0] : "";
                String title = parts.length > 1 ? parts[1] : "";
                String uploader = parts.length > 2 ? parts[2] : "";
                String videoId = parts.length > 3 ? parts[3] : "";
                String directUrl = parts.length > 4 ? parts[4] : "";

                if (directUrl.isBlank() || "None".equals(directUrl)) {
                    directUrl = videoId.isBlank() ? "" : "https://www.jiosaavn.com/song/" + videoId;
                }
                
                if (index.isBlank() || "None".equals(index)) {
                    index = String.valueOf(i + 1);
                }

                Song song = new Song(index, title, uploader, directUrl);
                songs.add(song);

                String globalKey = videoId.isBlank() ? ("global-song-" + index) : videoId;
                String payload = String.format("{\"key\":\"%s\",\"name\":\"%s\",\"url\":\"%s\",\"id\":\"%s\"}",
                        globalKey,
                        title.replace("\"", "\\\""),
                        directUrl,
                        videoId.isBlank() ? globalKey : videoId);

                kafkaTemplate.send(TOPIC_NAME, globalKey, payload);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return songs;
    }
}
