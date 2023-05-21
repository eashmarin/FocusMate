package com.api.focusmate.controller;

import com.api.focusmate.model.Limits;
import com.api.focusmate.model.User;
import com.api.focusmate.service.LimitsService;
import com.api.focusmate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/limits")
public class LimitsController {
    private final LimitsService limitsService;

    @Autowired
    public LimitsController(LimitsService limitsService) {
        this.limitsService = limitsService;
    }

    @GetMapping
    public Iterable<Limits> getAll() {
        return limitsService.getAllLimits();
    }

    @GetMapping("{id}")
    public Limits getById(@PathVariable long id) {
        return limitsService.getLimitsById(id);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        limitsService.deleteLimits(id);
    }

    @PostMapping
    public Limits addLimits(@RequestBody Limits user) {
        return limitsService.saveLimits(user);
    }
}
