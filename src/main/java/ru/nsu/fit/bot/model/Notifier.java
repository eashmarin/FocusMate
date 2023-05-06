package ru.nsu.fit.bot.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "notifier")
public class Notifier {
    @Id
    @NotNull
    private Long id;
    @NotNull
    private String notification;
}