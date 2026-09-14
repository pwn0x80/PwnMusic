package com.telegram.songBot.songBot.consumer;

import com.telegram.songBot.songBot.model.DownloadedSong;
import com.telegram.songBot.songBot.repository.DownloadedSongRepository;
import com.telegram.songBot.songBot.service.TelegramService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SongKafkaConsumer {

    private static final String DOWNLOAD_DIR = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "SongBotDownloads";
    private final TelegramService telegramService;
    private final DownloadedSongRepository downloadedSongRepository;

    @Autowired
    public SongKafkaConsumer(TelegramService telegramService, DownloadedSongRepository downloadedSongRepository) {
        this.telegramService = telegramService;
        this.downloadedSongRepository = downloadedSongRepository;
    }

    @KafkaListener(topics = "songTopic", groupId = "song-bot-group")
    public void consumeSong(ConsumerRecord<String, String> record) {
        String jsonPayload = record.value();
        String url = extractField(jsonPayload, "url");
        String name = extractField(jsonPayload, "name");
        String songId = extractField(jsonPayload, "id");

        if (songId == null || songId.isBlank()) {
            songId = record.key();
        }

        if (url == null || url.isBlank()) {
            System.out.println("Skipping download: No valid URL found in message for key: " + record.key());
            return;
        }

        // Clean URL to remove smuggling parameters for DB storage
        String cleanUrl = url.contains("#") ? url.substring(0, url.indexOf('#')) : url;

        // Check in PostgreSQL/H2 database if song already exists
        if (downloadedSongRepository.existsById(songId)) {
            System.out.println("[SKIPPED] Song already exists in database (ID: " + songId + ", Title: " + name + "). Skipping download.");
            return;
        }

        File dir = new File(DOWNLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        System.out.println("[DOWNLOAD STARTED] Song: " + name + " | ID: " + songId + " | URL: " + url + " (Best Audio Quality via yt-dlp -f bestaudio --embed-thumbnail)");

        File audioFile = null;
        File thumbFile = null;

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "yt-dlp",
                    "-f", "bestaudio",
                    "--embed-thumbnail",
                    "--embed-metadata",
                    "--write-thumbnail",
                    "--convert-thumbnails", "jpg",
                    "-o", DOWNLOAD_DIR + File.separator + "%(title)s.%(ext)s",
                    url
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("Destination:")) {
                        String dest = line.substring(line.indexOf("Destination:") + 12).trim();
                        audioFile = new File(dest);
                    }
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("[DOWNLOAD COMPLETED] Successfully downloaded in best quality: " + name);

                if (audioFile == null || !audioFile.exists()) {
                    audioFile = findLatestAudioFile(new File(DOWNLOAD_DIR));
                }
                
                if (audioFile != null) {
                    String baseName = audioFile.getName().contains(".") ? audioFile.getName().substring(0, audioFile.getName().lastIndexOf('.')) : audioFile.getName();
                    File potentialThumb = new File(DOWNLOAD_DIR, baseName + ".jpg");
                    if (potentialThumb.exists()) {
                        thumbFile = potentialThumb;
                    }
                }
                if (thumbFile == null || !thumbFile.exists()) {
                    thumbFile = findLatestFileByExtension(new File(DOWNLOAD_DIR), ".jpg");
                }

                if (audioFile != null && audioFile.exists()) {
                    System.out.println("[TELEGRAM UPLOAD STARTED] Sending " + audioFile.getName() + " with artwork to Telegram topic...");
                    boolean sent = telegramService.sendAudio(audioFile, thumbFile, "🎵 " + name, name, "JioSaavn");
                    if (sent) {
                        System.out.println("[TELEGRAM UPLOAD COMPLETED] Successfully sent to Telegram.");
                        
                        // Save successfully downloaded song record in database with clean URL
                        DownloadedSong downloadedSong = new DownloadedSong(songId, name, "JioSaavn", cleanUrl);
                        downloadedSongRepository.save(downloadedSong);
                        System.out.println("[DB SAVED] Song recorded in database.");
                    } else {
                        System.out.println("[TELEGRAM UPLOAD FAILED] Could not send to Telegram.");
                    }
                } else {
                    System.out.println("[TELEGRAM UPLOAD SKIPPED] Audio file could not be located on disk.");
                }

            } else {
                System.out.println("[DOWNLOAD FAILED] yt-dlp exited with code " + exitCode + " for: " + name);
            }

        } catch (Exception e) {
            System.err.println("[DOWNLOAD ERROR] Failed to download " + name + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (audioFile != null && audioFile.exists()) {
                audioFile.delete();
            }
            if (thumbFile != null && thumbFile.exists()) {
                thumbFile.delete();
            }
        }

        // 5-second cooldown between downloads
        System.out.println("[COOLDOWN] Taking a 5-second break before the next download...");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private String extractField(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private File findLatestAudioFile(File folder) {
        File[] files = folder.listFiles((dir, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".m4a") || lower.endsWith(".webm") || lower.endsWith(".opus") || lower.endsWith(".mp3") || lower.endsWith(".flac");
        });
        if (files == null || files.length == 0) {
            return null;
        }
        File latest = files[0];
        for (File f : files) {
            if (f.lastModified() > latest.lastModified()) {
                latest = f;
            }
        }
        return latest;
    }

    private File findLatestFileByExtension(File folder, String extension) {
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(extension));
        if (files == null || files.length == 0) {
            return null;
        }
        File latest = files[0];
        for (File f : files) {
            if (f.lastModified() > latest.lastModified()) {
                latest = f;
            }
        }
        return latest;
    }
}
