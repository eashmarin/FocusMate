package com.api.focusmate.service;

import com.api.focusmate.rerository.UserRepository;
import com.api.focusmate.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByToken(Integer token) {
        return userRepository.findByToken(token);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User saveUser(User user) {
        if (user.getId() != null && userRepository.existsById(user.getId())) {
            User existingUser = userRepository.findById(user.getId()).orElse(null);
            if (existingUser != null) {
                existingUser.setToken(user.getToken());
                existingUser.setTelegram(user.getTelegram());
                return userRepository.save(existingUser);
            }
        }
        return userRepository.save(user);
    }
}