package com.api.focusmate.repository;

import com.api.focusmate.model.Limit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LimitRepository extends CrudRepository<Limit, Long> {
    @Query("SELECT l FROM Limit l WHERE l.user.id = :userId")
    Iterable<Limit> findByUserId(Long userId);
}
