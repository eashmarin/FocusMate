package com.api.focusmate.controller;

import com.api.focusmate.exception.LimitNotFoundException;
import com.api.focusmate.exception.UserNotFoundException;
import com.api.focusmate.exception.UsingUnspecifiedFunctionException;
import com.api.focusmate.model.Limit;
import com.api.focusmate.model.User;
import com.api.focusmate.service.LimitService;
import com.api.focusmate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * This class represents the controller for managing User Token Limits.
 * It contains methods for adding, getting, changing, and deleting limits by user token.
 *
 * @author Knyazhev Misha
 * @version 1.0
 * @since 23.05.23
 */
@RestController
@ControllerAdvice(basePackages = "com.api.focusmate.controller")
@RequestMapping("/user")
public class UserTokenLimitController {
    /**
     * The UserService used to interact with User data.
     */
    private final UserService userService;
    /**
     * The LimitService used to interact with Limit data.
     */
    private final LimitService limitService;

    @Autowired
    public UserTokenLimitController(UserService userService, LimitService limitService) {
        this.userService = userService;
        this.limitService = limitService;
    }

    /**
     * Adds a limit for a user with the given token.
     *
     * @param token the user token
     * @param limit the limit to be added
     * @throws UserNotFoundException if user with the given token is not found
     */
    @PostMapping("{token}/limit")
    public void addLimit(@PathVariable String token, @RequestBody Limit limit){
        User user = userService.getUserByToken(token);
        if(user == null){
            throw new UserNotFoundException("Пользователь не найден");
        }
        else {
            limit.setUser(user);
            limitService.saveLimits(limit);
        }
    }

    /**
     * Gets all limits for a user with the given token.
     *
     * @param token the user token
     * @return the iterable list of all limits for the user
     * @throws UserNotFoundException if user with the given token is not found
     */
    @GetMapping("{token}/limit")
    public Iterable<Limit> getAllLimitsByToken(@PathVariable String token) {
        User user = userService.getUserByToken(token);
        if(user == null){
            throw new UserNotFoundException("Пользователь не найден");
        }
        else {
            Long id = userService.getUserByToken(token).getId();
            return limitService.getAllLimitsByUserId(id);
        }
    }

    /**
     * Changes a limit for a user with the given token.
     *
     * @param token the user token
     * @param limit the limit to be changed
     * @throws UserNotFoundException if user with the given token is not found
     * @throws LimitNotFoundException if limit with the given url is not found for the user
     */
    @PutMapping("{token}/limit")
    public void changeLimit(@PathVariable String token, @RequestBody Limit limit){
        User user = userService.getUserByToken(token);
        if(user == null){
            throw new UserNotFoundException("Пользователь не найден");
        }
        else {
            Iterable<Limit> limits = limitService.getAllLimitsByUserId(user.getId());
            Limit foundLimit = null;

            for (Limit l : limits) {
                if (l.getUrl().equals(limit.getUrl())) {
                    foundLimit = l;
                    break;
                }
            }

            if(foundLimit == null){
                throw new LimitNotFoundException("Лимит с " + limit.getUrl() + " не найден");
            }
            else {
                limit.setId(foundLimit.getId());
                limit.setUser(foundLimit.getUser());
                limitService.editLimit(limit);
            }
        }
    }

    /**
     * Throws an UsingUnspecifiedFunctionException for the unsupported HTTP method.
     *
     * @throws UsingUnspecifiedFunctionException if called, as the function is not specified
     */
    @PatchMapping("{token}/limit")
    public void patch(){
        throw new UsingUnspecifiedFunctionException("Не определена");
    }

    /**
     * Throws an UsingUnspecifiedFunctionException for the unsupported HTTP method.
     *
     * @throws UsingUnspecifiedFunctionException if called, as the function is not specified
     */
    @DeleteMapping("{token}/limit")
    public void delete(){
        throw new UsingUnspecifiedFunctionException("Не определена");
    }
}