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
 * Append-only audit of TP changes; {@code idempotencyKey} prevents double-award on retries.
 */
@Entity
@Table(name = "tournament_point_event")
public class TournamentPointEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private TournamentEnrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "week_id", nullable = false)
    private CompeteWeek week;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 24)
    private CompeteTpSourceType sourceType;

    @Column(name = "source_key", nullable = false, length = 160)
    private String sourceKey;

    @Column(name = "delta_tp", nullable = false)
    private int deltaTp;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 128)
    private String idempotencyKey;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    protected TournamentPointEvent() {
    }

    public TournamentPointEvent(TournamentEnrollment enrollment, User user, CompeteWeek week,
                                CompeteTpSourceType sourceType, String sourceKey, int deltaTp,
                                String idempotencyKey) {
        this.enrollment = enrollment;
        this.user = user;
        this.week = week;
        this.sourceType = sourceType;
        this.sourceKey = sourceKey;
        this.deltaTp = deltaTp;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public TournamentEnrollment getEnrollment() {
        return enrollment;
    }

    public User getUser() {
        return user;
    }

    public CompeteWeek getWeek() {
        return week;
    }

    public CompeteTpSourceType getSourceType() {
        return sourceType;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public int getDeltaTp() {
        return deltaTp;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
