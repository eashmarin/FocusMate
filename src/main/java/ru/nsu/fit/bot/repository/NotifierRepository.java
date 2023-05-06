package ru.nsu.fit.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nsu.fit.bot.model.Notifier;

public interface NotifierRepository extends JpaRepository<Notifier, Long> {
}
