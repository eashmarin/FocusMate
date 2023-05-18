package ru.nsu.fit.bot.model;

import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * This is entity from database
 * It contains some user info from telegram such as
 * @author Marina Senoshenko
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_data")
public class User {
    /**
     * special telegram id
     */
    @Id
    @NotNull
    private Long chatId;
    /**
     * name in telegram
     */
    @NotNull
    private String firstName;
    /**
     * surname in telegram
     */
    @NotNull
    private String lastName;
    /**
     * special name, which user write in telegram
     */
    @NotNull
    private String userName;
    /**
     * time of registration in bot
     */
    @NotNull
    private Timestamp registeredAt;
}