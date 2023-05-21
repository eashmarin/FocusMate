package com.api.focusmate.rerository;

import com.api.focusmate.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByToken(Integer token);
}