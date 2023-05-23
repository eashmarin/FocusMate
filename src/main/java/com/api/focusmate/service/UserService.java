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

    public Long getTelegramByToken(String token) {
        return userRepository.findByToken(token).getTelegram();
    }
    public User getUserByToken(String token) {
        return userRepository.findByToken(token);
    }

    public void deleteUser(User user) {
        userRepository.deleteById(user.getId());
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User editUser(User user){
        User existingUser = userRepository.findById(user.getId()).orElse(null);

        existingUser.setToken(user.getToken());
        existingUser.setTelegram(user.getTelegram());
        return userRepository.save(existingUser);
    }
}