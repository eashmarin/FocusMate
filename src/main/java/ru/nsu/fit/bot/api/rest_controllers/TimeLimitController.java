package ru.nsu.fit.bot.api.rest_controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nsu.fit.bot.api.service.TelegramBotService;
import ru.nsu.fit.bot.model.*;

@RestController
@AllArgsConstructor
@RequestMapping("/timelimitincrease")
public class TimeLimitController {
    private TelegramBotService telegramBotService;

    @PostMapping("")
    private User timeLimitIncrease(@RequestParam("chatId") Long chatId) {
        return telegramBotService.timeLimitIncrease(chatId);
    }
}
