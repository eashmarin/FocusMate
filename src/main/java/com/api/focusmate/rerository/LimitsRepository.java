package com.api.focusmate.rerository;

import com.api.focusmate.model.Limits;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LimitsRepository extends CrudRepository<Limits, Long> {
    @Query("SELECT l FROM Limits l WHERE l.user.id = :userId")
    Iterable<Limits> findByUserId(Long userId);
}
