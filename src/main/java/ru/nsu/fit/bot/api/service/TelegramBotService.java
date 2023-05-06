package ru.nsu.fit.bot.api.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.nsu.fit.bot.config.BotConfig;
import ru.nsu.fit.bot.model.Notifier;
import ru.nsu.fit.bot.repository.*;
import ru.nsu.fit.bot.model.User;

import java.sql.Timestamp;
import java.util.*;

@Service
public class TelegramBotService extends TelegramLongPollingBot {
    private final BotConfig config;
    private final UserRepository userRepository;
    private final NotifierRepository notifierRepository;
    public static Logger log = Logger.getLogger(TelegramBotService.class.getName());
    private static final String HELP_TEXT = """
            This bot was made for the student project

            Press /start to get welcome message from this bot

            Press /mydata to get your data which this bot saved

            Press /deletedata to delete your data from bot

            """;

    public TelegramBotService(BotConfig config, UserRepository userRepository, NotifierRepository notifierRepository) {
        this.config = config;
        this.userRepository = userRepository;
        this.notifierRepository = notifierRepository;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get welcome message"));
        listOfCommands.add(new BotCommand("/mydata", "get your data stored"));
        listOfCommands.add(new BotCommand("/deletedata", "delete your data"));
        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
        try {
            execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start" -> {
                    saveUserInfo(update.getMessage());
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                }
                case "/mydata" -> {
                    User user = userRepository.findByChatId(chatId);
                    if (user != null) {
                        sendMessage(chatId, "There is your saved data:\n\n" +
                                "Chat id: " + user.getChatId() +
                                "\nFirst name: " + user.getFirstName() +
                                "\nLast name: " + user.getLastName() +
                                "\nUser name: " + user.getUserName() +
                                "\nRegistered at: " + user.getRegisteredAt(), "/mydata");
                    }
                    else {
                        sendMessage(chatId, "There is no your saved data now", "mydata");
                    }
                }
                case "/deletedata" -> deleteUserInfo(update.getMessage());
                case "/help" -> sendMessage(chatId, HELP_TEXT, "/help");
                default -> sendMessage(chatId, "Command not recognized", "");
            }
        }
    }

    private void saveUserInfo(Message message) {
        Long chatId = message.getChatId();
        if (userRepository.findByChatId(chatId) == null) {
            var chat = message.getChat();
            User user = new User(chatId, chat.getFirstName(), chat.getLastName(), chat.getUserName(),
                    new Timestamp(System.currentTimeMillis()));
            userRepository.save(user);
            log.info("Save user with chat id = " + user.getChatId());
        }
    }

    private void deleteUserInfo(Message message) {
        Long chatId = message.getChatId();
        User user = userRepository.findByChatId(chatId);
        String messageForUser = "There is no your saved data now";
        if (user != null) {
            userRepository.delete(user);
            if (userRepository.findByChatId(chatId) == null) {
                messageForUser = "Your data was successfully deleted!";
                log.info("Delete user with chat id = " + chatId);
            }
            else {
                messageForUser = "Sorry, but there was an error when delete your data.\n" +
                        "Press /deletedata to try again";
                log.error("Can't delete user with chat id = " + chatId);
            }
        }
        sendMessage(chatId, messageForUser, "/deletedata");
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you";
        sendMessage(chatId, answer, "/start");
    }

    private void sendMessage(long chatId, String textToSend, String commandName) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
            log.info("Execute command " + commandName + " for user with chat id = " + chatId);
        } catch (TelegramApiException e) {
            throw new RuntimeException();
        }
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    public User timeLimitIncrease(Long chatId) {
        var user = userRepository.findByChatId(chatId);
        var notifiers = notifierRepository.findAll();
        for (Notifier notifier : notifiers) {
            prepareAndSendMessage(user.getChatId(), notifier.getNotification());
            log.info("User with chat id = " + chatId + " increased time limit");
        }
        return user;
    }
}
