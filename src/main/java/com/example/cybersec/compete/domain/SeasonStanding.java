package com.example.cybersec.compete.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * Running season points (SP) per user. Composite key matches FK columns to users / compete_season.
 */
@Entity
@Table(name = "season_standing")
@IdClass(SeasonStanding.SeasonStandingId.class)
public class SeasonStanding {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "season_id", nullable = false)
    private Long seasonId;

    @Column(name = "sp_total", nullable = false)
    private int spTotal;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected SeasonStanding() {
    }

    public SeasonStanding(Long userId, Long seasonId, int spTotal) {
        this.userId = userId;
        this.seasonId = seasonId;
        this.spTotal = spTotal;
        this.updatedAt = Instant.now();
    }

    public Long getUserId() {
        return userId;
    }

    public Long getSeasonId() {
        return seasonId;
    }

    public int getSpTotal() {
        return spTotal;
    }

    public void setSpTotal(int spTotal) {
        this.spTotal = spTotal;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class SeasonStandingId implements Serializable {
        private Long userId;
        private Long seasonId;

        public SeasonStandingId() {
        }

        public SeasonStandingId(Long userId, Long seasonId) {
            this.userId = userId;
            this.seasonId = seasonId;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getSeasonId() {
            return seasonId;
        }

        public void setSeasonId(Long seasonId) {
            this.seasonId = seasonId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SeasonStandingId that = (SeasonStandingId) o;
            return Objects.equals(userId, that.userId) && Objects.equals(seasonId, that.seasonId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, seasonId);
        }
    }
}
