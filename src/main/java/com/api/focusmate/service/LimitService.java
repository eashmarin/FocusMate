package com.api.focusmate.service;

import com.api.focusmate.model.Limit;
import com.api.focusmate.repository.LimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimitService {
    private LimitRepository limitRepository;

    @Autowired
    public LimitService(LimitRepository limitRepository) {
        this.limitRepository = limitRepository;
    }

    public Iterable<Limit> getAllLimits() {
        return limitRepository.findAll();
    }

    public Limit getLimitsById(Long id) {
        return limitRepository.findById(id).orElse(null);
    }

    public Limit saveLimits(Limit limit) {
        return limitRepository.save(limit);
    }

    public Limit editLimit(Limit limit){
        Limit existingLimit = limitRepository.findById(limit.getId()).orElse(null);

        existingLimit.setUrl(limit.getUrl());
        existingLimit.setLimitTime(limit.getLimitTime());
        existingLimit.setUser(limit.getUser());
        return limitRepository.save(existingLimit);

    }

    public void deleteLimits(Long id) {
        limitRepository.deleteById(id);
    }

    public Iterable<Limit> getAllLimitsByUserId(Long userId) {
        return limitRepository.findByUserId(userId);
    }
}
