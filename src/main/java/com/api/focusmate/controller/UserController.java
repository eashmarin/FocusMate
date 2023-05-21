package com.api.focusmate.controller;

import com.api.focusmate.model.Limits;
import com.api.focusmate.model.User;
import com.api.focusmate.service.LimitsService;
import com.api.focusmate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final LimitsService limitsService;

    @Autowired
    public UserController(UserService userService, LimitsService limitsService) {
        this.userService = userService;
        this.limitsService = limitsService;
    }

    @GetMapping
    public Iterable<User> getAll() {
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    public User getById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @GetMapping("{id}/limits")
    public Iterable<Limits> getAllLimitsById(@PathVariable long id) {
        return limitsService.getAllLimitsByUserId(id);
    }

    @GetMapping("{token}/limits")
    public Iterable<Limits> getAllLimitsByToken(@PathVariable int token) {
        Long id = userService.getUserByToken(token).getId();
        return limitsService.getAllLimitsByUserId(id);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
         userService.deleteUser(id);
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        return userService.saveUser(user);
    }


}