package com.example.cybersec.compete.repository;

import com.example.cybersec.compete.domain.UserCompeteProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCompeteProfileRepository extends JpaRepository<UserCompeteProfile, Long> {
}
