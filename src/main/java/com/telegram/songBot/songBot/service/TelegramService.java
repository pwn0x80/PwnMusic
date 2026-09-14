package com.telegram.songBot.songBot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Service
public class TelegramService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.chat-id}")
    private String chatId;

    @Value("${telegram.bot.message-thread-id}")
    private String messageThreadId;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean sendAudio(File mp3File, File thumbnailFile, String caption, String title, String performer) {
        try {
            String url = "https://api.telegram.org/bot" + botToken + "/sendAudio";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("chat_id", chatId);
            if (messageThreadId != null && !messageThreadId.isBlank()) {
                body.add("message_thread_id", messageThreadId);
            }
            body.add("caption", caption);
            if (title != null && !title.isBlank()) {
                body.add("title", title);
            }
            if (performer != null && !performer.isBlank()) {
                body.add("performer", performer);
            }
            body.add("audio", new FileSystemResource(mp3File));
            
            if (thumbnailFile != null && thumbnailFile.exists()) {
                body.add("thumb", new FileSystemResource(thumbnailFile));
            }

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Failed to send audio to Telegram: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
