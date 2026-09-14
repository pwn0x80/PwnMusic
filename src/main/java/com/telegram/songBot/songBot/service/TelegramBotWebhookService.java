package com.telegram.songBot.songBot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TelegramBotWebhookService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.chat-id}")
    private String chatId;

    @Value("${telegram.bot.message-thread-id}")
    private String messageThreadId;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendTriggerMessage() {
        try {
            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

            String jsonPayload = String.format(
                    "{\"chat_id\": \"%s\", \"message_thread_id\": \"%s\", \"text\": \"📥 To start downloading JioSaavn Chartbusters, please visit/open your backend trigger URL:\\nhttp://localhost:8080/trigger-download\"}",
                    chatId, messageThreadId
            );

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

            org.springframework.http.HttpEntity<String> request = new org.springframework.http.HttpEntity<>(jsonPayload, headers);
            restTemplate.postForEntity(url, request, String.class);
            System.out.println("[TELEGRAM] Trigger notification message sent successfully to group/topic.");
        } catch (Exception e) {
            System.err.println("Failed to send Telegram message: " + e.getMessage());
        }
    }
}
