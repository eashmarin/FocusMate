package com.api.focusmate.controller;

import com.api.focusmate.model.Limit;
import com.api.focusmate.model.User;
import com.api.focusmate.service.LimitService;
import com.api.focusmate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Base64;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final LimitService limitService;

    @Autowired
    public UserController(UserService userService, LimitService limitService) {
        this.userService = userService;
        this.limitService = limitService;
    }

    @GetMapping("{token}/limit")
    public Iterable<Limit> getAllLimitsByToken(@PathVariable String token) {
        Long id = userService.getUserByToken(token).getId();
        return limitService.getAllLimitsByUserId(id);
    }

    @GetMapping("{token}/telegram")
    public Long getTelegramByToken(@PathVariable String token) {
        return userService.getTelegramByToken(token);
    }

    @DeleteMapping("{token}")
    public void delete(@PathVariable String token) {
         userService.deleteUser(token);
    }

    @PostMapping("{token}/limit")
    public void addLimit(@PathVariable String token, @RequestBody Limit limit){
        User user = userService.getUserByToken(token);
        limit.setUser(user);
        limitService.saveLimits(limit);
    }

    @PutMapping("{token}/telegram/{telegram}")
    public void addLimit(@PathVariable String token, @PathVariable Long telegram){
        User user = userService.getUserByToken(token);
        user.setTelegram(telegram);
        userService.editUser(user);
    }

    @PutMapping("{token}/limit/{url}")
    public void changeLimit(@PathVariable String token, @RequestBody Limit limit, @PathVariable String url){
        Iterable<Limit> limits = limitService.getAllLimitsByUserId(userService.getUserByToken(token).getId());
        Limit foundLimit = null;

        for (Limit l : limits) {
            if (l.getUrl().equals(url)) {
                foundLimit = limit;
                break;
            }
        }

        limit.setId(foundLimit.getId());
        limitService.editLimit(limit);
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
}