package com.example.cybersec.compete.service;

import com.example.cybersec.compete.domain.CompeteBracket;
import com.example.cybersec.compete.domain.UserCompeteProfile;
import com.example.cybersec.compete.repository.UserCompeteProfileRepository;
import com.example.cybersec.user.entity.User;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Creates default Compete preferences and keeps timezone band in sync (coarse cohorts).
 */
@Service
public class UserCompeteProfileService {

    private final UserCompeteProfileRepository profileRepository;
    private final EntityManager entityManager;

    public UserCompeteProfileService(UserCompeteProfileRepository profileRepository, EntityManager entityManager) {
        this.profileRepository = profileRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public UserCompeteProfile getOrCreate(User user) {
        return profileRepository.findById(user.getId()).orElseGet(() -> {
            UserCompeteProfile p = new UserCompeteProfile(user);
            p.setTimezoneBand(deriveTimezoneBand(p.getTimezone()));
            entityManager.persist(p);
            return p;
        });
    }

    @Transactional
    public UserCompeteProfile updateTimezone(User user, String timezone) {
        UserCompeteProfile p = getOrCreate(user);
        p.setTimezone(timezone);
        p.setTimezoneBand(deriveTimezoneBand(timezone));
        p.setUpdatedAt(Instant.now());
        return profileRepository.save(p);
    }

    @Transactional
    public UserCompeteProfile updateFlags(User user, Boolean autoEnroll) {
        UserCompeteProfile p = getOrCreate(user);
        if (autoEnroll != null) {
            p.setAutoEnroll(autoEnroll);
        }
        p.setUpdatedAt(Instant.now());
        return profileRepository.save(p);
    }

    @Transactional
    public UserCompeteProfile updateBracket(User user, CompeteBracket bracket) {
        UserCompeteProfile p = getOrCreate(user);
        if (bracket != null) {
            p.setBracket(bracket);
        }
        p.setUpdatedAt(Instant.now());
        return profileRepository.save(p);
    }

    /**
     * Maps offset hours to AM1 / AM2 / AM3 bands (MVP heuristic).
     */
    public String deriveTimezoneBand(String timezone) {
        try {
            ZoneId z = ZoneId.of(timezone);
            ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.now(), z);
            int hours = zdt.getOffset().getTotalSeconds() / 3600;
            if (hours <= -4) {
                return "AM1";
            }
            if (hours <= 3) {
                return "AM2";
            }
            return "AM3";
        } catch (Exception ex) {
            return "AM2";
        }
    }
}
