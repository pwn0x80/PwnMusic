package com.telegram.songBot.songBot.repository;

import com.telegram.songBot.songBot.model.DownloadedSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DownloadedSongRepository extends JpaRepository<DownloadedSong, String> {
}
