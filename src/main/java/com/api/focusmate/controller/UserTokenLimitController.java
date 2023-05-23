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

@RestController
@ControllerAdvice(basePackages = "com.api.focusmate.controller")
@RequestMapping("/user")
public class UserTokenLimitController {
    private final UserService userService;
    private final LimitService limitService;

    @Autowired
    public UserTokenLimitController(UserService userService, LimitService limitService) {
        this.userService = userService;
        this.limitService = limitService;
    }

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

    @PatchMapping("{token}/limit")
    public void patch(){
        throw new UsingUnspecifiedFunctionException("Не определена");
    }

    @DeleteMapping("{token}/limit")
    public void delete(){
        throw new UsingUnspecifiedFunctionException("Не определена");
    }
}