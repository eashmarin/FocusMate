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

/**
 * This is a bot service which execute user's commands
 * such as /start, /mydata, /deletedata
 * @author Marina Senoshenko
 */
@Service
public class TelegramBotService extends TelegramLongPollingBot {
    /**
     * This is a bot configuration, includes telegram bot token and name
     * To more information see {@link BotConfig}
     */
    private final BotConfig config;
    /**
     * This is a user repository, which saves user data to database
     * To more information see {@link UserRepository}
     */
    private final UserRepository userRepository;
    /**
     * This is a repository, which contains notification message for user
     * To more information see {@link NotifierRepository}
     */
    private final NotifierRepository notifierRepository;
    /**
     * This is a logger, which saves some important info such as
     * exceptions and user's actions
     */
    public static Logger log = Logger.getLogger(TelegramBotService.class.getName());
    /**
     * This is a help text for user
     */
    private static final String HELP_TEXT = """
            This bot was made for the student project

            Press /start to get welcome message from this bot

            Press /mydata to get your data which this bot saved

            Press /deletedata to delete your data from bot

            """;

    /**
     * This method organises menu with commands in telegram
     * and logs {@link TelegramApiException} if it throws
     * @param config {@link BotConfig}
     * @param userRepository {@link UserRepository}
     * @param notifierRepository {@link NotifierRepository}
     */
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

    /**
     * This method call's when user type some text
     * In command /start user info saves
     * In command /mydata user gets his saved info
     * In command /deletedata data associated with this user deletes
     * In command /help user gets all commands with descriptions
     * @param update Update received
     */
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

    /**
     * This method save user info to database by {@link UserRepository}
     * @param message telegram structure with user's info
     */
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

    /**
     * This method deletes user info if it is already in database
     * or send message for user that there is no saved info
     * @param message telegram structure with user's info
     */
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

    /**
     * This method send hello message to user
     * @param chatId telegram id of user
     * @param name user name in telegram
     */
    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you";
        sendMessage(chatId, answer, "/start");
    }

    /**
     * This method send message for user
     * @param chatId telegram id of user
     * @param textToSend text for user
     * @param commandName command which executes now
     * @throws RuntimeException if message can't be executed
     */
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

    /**
     * Execute message if it is possible and log errors if exception throws
     * @param message text for user
     */
    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * This method constructs message for user
     * @param chatId telegram id of user
     * @param textToSend text
     */
    private void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }

    /**
     * This method is a getter for bot name
     * @return botName
     */
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    /**
     * This method is a getter for bot token
     * @return botToken
     */
    @Override
    public String getBotToken() {
        return config.getToken();
    }

    /**
     * This method send message about time limit increase for user
     * @param chatId telegram id of user
     * @return user, who increase time limit
     */
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
