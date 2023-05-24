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

@RestController
@ControllerAdvice(basePackages = "com.api.focusmate.controller")
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final LimitService limitService;

    @Autowired
    public UserController(UserService userService, LimitService limitService) {
        this.userService = userService;
        this.limitService = limitService;
    }


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

    @PutMapping
    public void put(){
        throw new UsingUnspecifiedFunctionException("Не определена");
    }

    @PatchMapping
    public void patch(){
        throw new UsingUnspecifiedFunctionException("Не определена");
    }
}