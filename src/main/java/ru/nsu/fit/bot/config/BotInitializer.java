package ru.nsu.fit.bot.config;

import lombok.AllArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.nsu.fit.bot.api.service.TelegramBotService;

@Component
@AllArgsConstructor
public class BotInitializer {
    private TelegramBotService telegramBot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            new TelegramBotsApi(DefaultBotSession.class).registerBot(telegramBot);
        } catch (TelegramApiException e) {
            TelegramBotService.log.error(e.getMessage());
        }
    }
}