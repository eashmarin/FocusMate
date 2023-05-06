package ru.nsu.fit.bot.model;

import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_data")
public class User {
    @Id
    @NotNull
    private Long chatId;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String userName;
    @NotNull
    private Timestamp registeredAt;
}