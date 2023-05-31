package ru.nsu.fit.bot.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * This is entity from database
 * It contains spacial message for user who increase time limit
 * @author Marina Senoshenko
 */
@Entity
@Data
@Table(name = "notifier")
public class Notifier {
    /**
     * id of notification message
     */
    @Id
    @NotNull
    private Long id;
    /**
     * notification message
     */
    @NotNull
    private String notification;
}