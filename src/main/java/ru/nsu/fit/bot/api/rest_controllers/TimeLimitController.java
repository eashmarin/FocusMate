package ru.nsu.fit.bot.api.rest_controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nsu.fit.bot.api.service.TelegramBotService;
import ru.nsu.fit.bot.model.*;

/**
 * This is a class which gets information about screen time
 * limit increase by some user from backend and send this
 * information to {@link TelegramBotService}
 * @author Marina Senoshenko
 */
@RestController
@AllArgsConstructor
@RequestMapping("/timelimitincrease")
public class TimeLimitController {
    /**
     * This is a field of service, which send information to user
     */
    private TelegramBotService telegramBotService;

    /**
     * This method send information to TelegramBotService
     * @param chatId id of user
     * @return user, who increased time limit
     */
    @PostMapping("")
    private User timeLimitIncrease(@RequestParam("chatId") Long chatId) {
        return telegramBotService.timeLimitIncrease(chatId);
    }
}
