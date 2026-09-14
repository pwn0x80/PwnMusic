package com.telegram.songBot.songBot.runner;

import com.telegram.songBot.songBot.bot.PwnMusicBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

@Component
public class BotInitializer implements CommandLineRunner {

    private final PwnMusicBot pwnMusicBot;
    
    @Value("${telegram.bot.token}")
    private String botToken;

    public BotInitializer(PwnMusicBot pwnMusicBot) {
        this.pwnMusicBot = pwnMusicBot;
    }

    @Override
    public void run(String... args) {
        try {
            TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
            botsApplication.registerBot(botToken, pwnMusicBot);
            System.out.println("[TELEGRAM] PwnMusicBot successfully registered and started via LongPolling!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
