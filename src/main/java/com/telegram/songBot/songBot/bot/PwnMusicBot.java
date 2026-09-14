package com.telegram.songBot.songBot.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import com.telegram.songBot.songBot.model.Song;
import com.telegram.songBot.songBot.service.PlaylistService;

@Component
public class PwnMusicBot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
   private final PlaylistService playlistService;

   @Autowired 
   public PwnMusicBot(TelegramClient telegramClient,PlaylistService playlistService) {
        this.telegramClient = telegramClient;
                this.playlistService = playlistService;

    }

    @Override
public void consume(Update update) {

    // Button clicked
    if (update.hasCallbackQuery()) {

        String data = update.getCallbackQuery().getData();

        if ("Chartbusters".equals(data)) {

            System.out.println("Start Sync Chartbusters ");

            Long chatId =
                    update.getCallbackQuery()
                            .getMessage()
                            .getChatId();

            SendMessage sendMessage = new SendMessage(
                    String.valueOf(chatId),
                    "Start Sync Chartbusters"
            );

            try {

                telegramClient.execute(sendMessage);
                  playlistService.fetchPlaylistSongs();

            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        return;
    }

    // Normal message
    if (update.hasMessage() && update.getMessage().hasText()) {

        String text = update.getMessage().getText();

        System.out.println("Received: " + text);

        Long chatId = update.getMessage().getChatId();

        if ("/start".equals(text)) {

            InlineKeyboardButton button =
                    InlineKeyboardButton.builder()
                            .text("Sync Chartbusters 2026 - International(Saavan)")
                            .callbackData("Chartbusters")
                            .build();

            InlineKeyboardRow row = new InlineKeyboardRow();
            row.add(button);

            InlineKeyboardMarkup keyboard =
                    InlineKeyboardMarkup.builder()
                            .keyboardRow(row)
                            .build();

            SendMessage message =
                    SendMessage.builder()
                            .chatId(chatId)
                            .text("pwnMusic option:")
                            .replyMarkup(keyboard)
                            .build();

            try {
                telegramClient.execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
    
}