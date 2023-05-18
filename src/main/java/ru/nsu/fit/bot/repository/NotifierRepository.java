package ru.nsu.fit.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nsu.fit.bot.model.Notifier;

/**
 * This is a class which saves notifications in repository
 * @author Marina Senoshenko
 */
public interface NotifierRepository extends JpaRepository<Notifier, Long> {
}
