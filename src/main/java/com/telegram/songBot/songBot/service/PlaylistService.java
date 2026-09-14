package com.telegram.songBot.songBot.service;

import com.telegram.songBot.songBot.model.Song;
import java.util.List;

public interface PlaylistService {
    List<Song> fetchPlaylistSongs();
}
