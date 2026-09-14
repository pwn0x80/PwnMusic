package com.telegram.songBot.songBot.runner;

import com.telegram.songBot.songBot.service.TelegramBotWebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BotStartupRunner implements CommandLineRunner {

    private final TelegramBotWebhookService webhookService;

    @Autowired
    public BotStartupRunner(TelegramBotWebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Override
    public void run(String... args) {
        webhookService.sendTriggerMessage();
    }
}
