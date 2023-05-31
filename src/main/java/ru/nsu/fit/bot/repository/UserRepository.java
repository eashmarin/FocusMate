package ru.nsu.fit.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nsu.fit.bot.model.User;

/**
 * This is a class which saves user info in repository
 * @author Marina Senoshenko
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * This method gets user info by chat id
     * @param chatId user id in telegram
     * @return user
     */
    User findByChatId(Long chatId);
}
