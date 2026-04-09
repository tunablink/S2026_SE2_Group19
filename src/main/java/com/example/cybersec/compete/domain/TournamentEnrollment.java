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
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

/**
 * Links a {@link User} to a {@link CompeteWeek} with running TP and leaderboard opt-in.
 */
@Entity
@Table(
        name = "tournament_enrollment",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "week_id"})
)
public class TournamentEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "week_id", nullable = false)
    private CompeteWeek week;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private CompeteBracket bracket;

    @Column(name = "timezone_band", nullable = false, length = 8)
    private String timezoneBand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private CompeteEnrollmentStatus status = CompeteEnrollmentStatus.ACTIVE;

    @Column(name = "tp_total", nullable = false)
    private int tpTotal;

    @Column(name = "opted_in_leaderboard", nullable = false)
    private boolean optedInLeaderboard;

    @Column(name = "first_tp_at")
    private Instant firstTpAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    protected TournamentEnrollment() {
    }

    public TournamentEnrollment(User user, CompeteWeek week, CompeteBracket bracket, String timezoneBand,
                                boolean optedInLeaderboard) {
        this.user = user;
        this.week = week;
        this.bracket = bracket;
        this.timezoneBand = timezoneBand;
        this.optedInLeaderboard = optedInLeaderboard;
        this.status = CompeteEnrollmentStatus.ACTIVE;
        this.tpTotal = 0;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CompeteWeek getWeek() {
        return week;
    }

    public void setWeek(CompeteWeek week) {
        this.week = week;
    }

    public CompeteBracket getBracket() {
        return bracket;
    }

    public void setBracket(CompeteBracket bracket) {
        this.bracket = bracket;
    }

    public String getTimezoneBand() {
        return timezoneBand;
    }

    public void setTimezoneBand(String timezoneBand) {
        this.timezoneBand = timezoneBand;
    }

    public CompeteEnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(CompeteEnrollmentStatus status) {
        this.status = status;
    }

    public int getTpTotal() {
        return tpTotal;
    }

    public void setTpTotal(int tpTotal) {
        this.tpTotal = tpTotal;
    }

    public boolean isOptedInLeaderboard() {
        return optedInLeaderboard;
    }

    public void setOptedInLeaderboard(boolean optedInLeaderboard) {
        this.optedInLeaderboard = optedInLeaderboard;
    }

    public Instant getFirstTpAt() {
        return firstTpAt;
    }

    public void setFirstTpAt(Instant firstTpAt) {
        this.firstTpAt = firstTpAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
