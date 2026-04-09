package com.example.cybersec.compete.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Immutable snapshot after week close for season points and history APIs.
 */
@Entity
@Table(name = "weekly_standing")
public class WeeklyStanding {

    @Id
    private Long enrollmentId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "enrollment_id")
    private TournamentEnrollment enrollment;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "week_id", nullable = false)
    private Long weekId;

    @Column(nullable = false, length = 16)
    private String bracket;

    @Column(name = "timezone_band", nullable = false, length = 8)
    private String timezoneBand;

    @Column(name = "tp_total", nullable = false)
    private int tpTotal;

    @Column(name = "rank_in_cohort", nullable = false)
    private int rankInCohort;

    @Column(name = "cohort_size", nullable = false)
    private int cohortSize;

    @Column(nullable = false, precision = 8, scale = 4)
    private BigDecimal percentile;

    @Column(name = "placement_points", nullable = false)
    private int placementPoints;

    @Column(name = "computed_at", nullable = false)
    private Instant computedAt = Instant.now();

    protected WeeklyStanding() {
    }

    public WeeklyStanding(TournamentEnrollment enrollment, Long userId, Long weekId, String bracket,
                          String timezoneBand, int tpTotal, int rankInCohort, int cohortSize,
                          BigDecimal percentile, int placementPoints) {
        this.enrollment = enrollment;
        this.enrollmentId = enrollment.getId();
        this.userId = userId;
        this.weekId = weekId;
        this.bracket = bracket;
        this.timezoneBand = timezoneBand;
        this.tpTotal = tpTotal;
        this.rankInCohort = rankInCohort;
        this.cohortSize = cohortSize;
        this.percentile = percentile;
        this.placementPoints = placementPoints;
        this.computedAt = Instant.now();
    }

    public Long getEnrollmentId() {
        return enrollmentId;
    }

    public TournamentEnrollment getEnrollment() {
        return enrollment;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getWeekId() {
        return weekId;
    }

    public String getBracket() {
        return bracket;
    }

    public String getTimezoneBand() {
        return timezoneBand;
    }

    public int getTpTotal() {
        return tpTotal;
    }

    public int getRankInCohort() {
        return rankInCohort;
    }

    public int getCohortSize() {
        return cohortSize;
    }

    public BigDecimal getPercentile() {
        return percentile;
    }

    public int getPlacementPoints() {
        return placementPoints;
    }

    public Instant getComputedAt() {
        return computedAt;
    }
}
