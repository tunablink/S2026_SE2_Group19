package com.example.cybersec.compete.repository;

import com.example.cybersec.compete.domain.CompeteRewardStatus;
import com.example.cybersec.compete.domain.RewardGrant;
import com.example.cybersec.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardGrantRepository extends JpaRepository<RewardGrant, Long> {

    List<RewardGrant> findByUserAndStatusOrderByCreatedAtDesc(User user, CompeteRewardStatus status);

    List<RewardGrant> findByUserOrderByCreatedAtDesc(User user);
}
