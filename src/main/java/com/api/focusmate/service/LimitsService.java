package com.api.focusmate.service;

import com.api.focusmate.model.Limits;
import com.api.focusmate.model.User;
import com.api.focusmate.rerository.LimitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimitsService {
    private LimitsRepository limitsRepository;

    @Autowired
    public LimitsService(LimitsRepository limitsRepository) {
        this.limitsRepository = limitsRepository;
    }

    public Iterable<Limits> getAllLimits() {
        return limitsRepository.findAll();
    }

    public Limits getLimitsById(Long id) {
        return limitsRepository.findById(id).orElse(null);
    }

    public Limits saveLimits(Limits limits) {
        if (limits.getId() != null && limitsRepository.existsById(limits.getId())) {
            Limits existingLimits = limitsRepository.findById(limits.getId()).orElse(null);
            if (existingLimits != null) {
                existingLimits.setUrl(limits.getUrl());
                existingLimits.setLimitTime(limits.getLimitTime());
                existingLimits.setUser(limits.getUser());
                return limitsRepository.save(existingLimits);
            }
        }
        return limitsRepository.save(limits);
    }

    public void deleteLimits(Long id) {
        limitsRepository.deleteById(id);
    }

    public Iterable<Limits> getAllLimitsByUserId(Long userId) {
        return limitsRepository.findByUserId(userId);
    }
}
