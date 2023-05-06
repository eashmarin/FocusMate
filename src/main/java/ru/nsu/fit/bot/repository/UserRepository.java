package ru.nsu.fit.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nsu.fit.bot.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByChatId(Long chatId);
}
