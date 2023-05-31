package com.api.focusmate.controller;

import com.api.focusmate.exception.UserNotFoundException;
import com.api.focusmate.exception.UsingUnspecifiedFunctionException;
import com.api.focusmate.model.User;
import com.api.focusmate.service.LimitService;
import com.api.focusmate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * This class represents a REST controller for managing users.
 * It handles HTTP requests related to creating, deleting and updating user information.
 *
 * @author Knyazhev Misha
 * @version 1.0
 * @since 23.05.23
 */
@RestController
@ControllerAdvice(basePackages = "com.api.focusmate.controller")
@RequestMapping("/user")
public class UserController {
    /**
     * The UserService used to interact with User data.
     */
    private final UserService userService;
    /**
     * The LimitService used to interact with Limit data.
     */
    private final LimitService limitService;

    @Autowired
    public UserController(UserService userService, LimitService limitService) {
        this.userService = userService;
        this.limitService = limitService;
    }


    /**
     * Deletes a user with the specified token.
     * Throws a UserNotFoundException if the user with the specified token is not found.
     *
     * @param token the token of the user to be deleted
     */
    @DeleteMapping("{token}")
    public void delete(@PathVariable String token) {
        User user = userService.getUserByToken(token);
        if(user == null){
            throw new UserNotFoundException("Пользователь не найден");
        }

        else {
            userService.deleteUser(user);
        }
    }

    /**
     * Creates a new user and generates a unique token for the user.
     *
     * @return the generated token for the new user
     */
    @PostMapping
    public String addUser() {
        User user = new User();

        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[24];
        secureRandom.nextBytes(tokenBytes);

        String token = null;
        User exist;
        boolean find = true;

        while (find) {
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes).substring(0, 24);
            exist = userService.getUserByToken(token);

            if(exist == null){
                find = false;
            }
        }
        user.setToken(token);
        userService.saveUser(user);
        return user.getToken();
    }

    /**
     * Throws an UsingUnspecifiedFunctionException for the unsupported HTTP method.
     *
     * @throws UsingUnspecifiedFunctionException if called, as the function is not specified
     */
    @PutMapping
    public void put(){
        throw new UsingUnspecifiedFunctionException("Не определена");
    }

    /**
     * Throws an UsingUnspecifiedFunctionException for the unsupported HTTP method.
     *
     * @throws UsingUnspecifiedFunctionException if called, as the function is not specified
     */
    @PatchMapping
    public void patch(){
        throw new UsingUnspecifiedFunctionException("Не определена");
    }
}