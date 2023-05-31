package com.api.focusmate.controller;

import com.api.focusmate.exception.UserNotFoundException;
import com.api.focusmate.exception.UsingUnspecifiedFunctionException;
import com.api.focusmate.model.User;
import com.api.focusmate.service.LimitService;
import com.api.focusmate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * The UserTokenTelegramController class handles HTTP requests related to user's Telegram ID.
 *
 * @author Knyazhev Misha
 * @version 1.0
 * @since 23.05.23
 */
@RestController
@ControllerAdvice(basePackages = "com.api.focusmate.controller")
@RequestMapping("/user")
public class UserTokenTelegramController {
    /**
     * The UserService used to interact with User data.
     */
    private final UserService userService;
    /**
     * The LimitService used to interact with Limit data.
     */
    private final LimitService limitService;

    @Autowired
    public UserTokenTelegramController(UserService userService, LimitService limitService) {
        this.userService = userService;
        this.limitService = limitService;
    }

    /**
     * Throws a UsingUnspecifiedFunctionException as this function is not specified.
     * @throws UsingUnspecifiedFunctionException if the function is not specified
     */
    @PostMapping("{token}/telegram")
    public void post(){
        throw new UsingUnspecifiedFunctionException("Не определена");
    }

    /**
     * Retrieves the Telegram ID associated with the user token and returns it.
     * Throws a UserNotFoundException if the user is not found.
     * @param token the user token
     * @return the Telegram ID associated with the user token
     * @throws UserNotFoundException if the user is not found
     */
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

    /**
     * Associates the specified Telegram ID with the user token.
     * Throws a UserNotFoundException if the user is not found.
     * @param token the user token
     * @param telegram the Telegram ID to associate with the user token
     * @throws UserNotFoundException if the user is not found
     */
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

    /**
     * Throws an UsingUnspecifiedFunctionException for the unsupported HTTP method.
     *
     * @throws UsingUnspecifiedFunctionException if the function is not specified
     */
    @PatchMapping("{token}/telegram")
    public void patch(){
        throw new UsingUnspecifiedFunctionException("Не определена");
    }

    /**
     * Throws an UsingUnspecifiedFunctionException for the unsupported HTTP method.
     *
     * @throws UsingUnspecifiedFunctionException if the function is not specified
     */
    @DeleteMapping("{token}/telegram")
    public void delete(){
        throw new UsingUnspecifiedFunctionException("Не определена");
    }
}
