package com.api.focusmate.controller;

import com.api.focusmate.exception.UserNotFoundException;
import com.api.focusmate.exception.UsingUnspecifiedFunctionException;
import com.api.focusmate.model.User;
import com.api.focusmate.service.LimitService;
import com.api.focusmate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@ControllerAdvice(basePackages = "com.api.focusmate.controller")
@RequestMapping("/user")
public class UserTokenTelegramController {
    private final UserService userService;
    private final LimitService limitService;

    @Autowired
    public UserTokenTelegramController(UserService userService, LimitService limitService) {
        this.userService = userService;
        this.limitService = limitService;
    }

    @PostMapping("{token}/telegram")
    public void post(){
        throw new UsingUnspecifiedFunctionException("Не определена");
    }

    @GetMapping("{token}/telegram")
    public Long getTelegramByToken(@PathVariable String token) {
        User user = userService.getUserByToken(token);
        if(user == null){
            throw new UserNotFoundException("Пользователь не найден");
        }
        else {
            return user.getTelegram();
        }
    }

    @PutMapping("{token}/telegram/{telegram}")
    public void addLimit(@PathVariable String token, @PathVariable Long telegram){
        User user = userService.getUserByToken(token);
        if(user == null){
            throw new UserNotFoundException("Пользователь не найден");
        }
        else {
            user.setTelegram(telegram);
            userService.editUser(user);
        }
    }

    @PatchMapping("{token}/telegram")
    public void patch(){
        throw new UsingUnspecifiedFunctionException("Не определена");
    }

    @DeleteMapping("{token}/telegram")
    public void delete(){
        throw new UsingUnspecifiedFunctionException("Не определена");
    }
}
