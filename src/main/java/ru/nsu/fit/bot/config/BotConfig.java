package ru.nsu.fit.bot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * This in a class with bot configuration
 * @author Marina Senoshenko
 */
@Data
@Configuration
@PropertySource("/application.properties")
public class BotConfig {
    /**
     * This is a bot name in telegram
     */
    @Value("${bot.name}")
    private String botName;
    /**
     * This is a bot special token in telegram
     */
    @Value("${bot.token}")
    private String token;
}