package com.example.cybersec.compete.domain;

import com.example.cybersec.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Claimable cosmetic / token payload after week close (JSON string in {@code payload} for MVP portability).
 */
@Entity
@Table(name = "reward_grant")
public class RewardGrant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "week_id")
    private CompeteWeek week;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id")
    private CompeteSeason season;

    @Column(nullable = false, length = 32)
    private String tier;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private CompeteRewardStatus status = CompeteRewardStatus.PENDING;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "claimed_at")
    private Instant claimedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    protected RewardGrant() {
    }

    public RewardGrant(User user, CompeteWeek week, CompeteSeason season, String tier, String payload,
                         Instant expiresAt) {
        this.user = user;
        this.week = week;
        this.season = season;
        this.tier = tier;
        this.payload = payload;
        this.expiresAt = expiresAt;
        this.status = CompeteRewardStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public CompeteWeek getWeek() {
        return week;
    }

    public CompeteSeason getSeason() {
        return season;
    }

    public String getTier() {
        return tier;
    }

    public String getPayload() {
        return payload;
    }

    public CompeteRewardStatus getStatus() {
        return status;
    }

    public void setStatus(CompeteRewardStatus status) {
        this.status = status;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getClaimedAt() {
        return claimedAt;
    }

    public void setClaimedAt(Instant claimedAt) {
        this.claimedAt = claimedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
