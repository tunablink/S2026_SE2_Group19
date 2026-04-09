package com.example.cybersec.compete.domain;

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
 * Canonical UTC tournament window (Monday start, Sunday end inclusive logic handled in service).
 */
@Entity
@Table(name = "compete_week")
public class CompeteWeek {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "season_id", nullable = false)
    private CompeteSeason season;

    @Column(name = "week_index", nullable = false)
    private int weekIndex;

    @Column(name = "week_start_utc", nullable = false, unique = true)
    private Instant weekStartUtc;

    @Column(name = "week_end_utc", nullable = false)
    private Instant weekEndUtc;

    @Column(nullable = false)
    private boolean frozen;

    @Enumerated(EnumType.STRING)
    @Column(name = "close_status", nullable = false, length = 16)
    private CompeteWeekCloseStatus closeStatus = CompeteWeekCloseStatus.OPEN;

    protected CompeteWeek() {
    }

    public CompeteWeek(CompeteSeason season, int weekIndex, Instant weekStartUtc, Instant weekEndUtc) {
        this.season = season;
        this.weekIndex = weekIndex;
        this.weekStartUtc = weekStartUtc;
        this.weekEndUtc = weekEndUtc;
        this.frozen = false;
        this.closeStatus = CompeteWeekCloseStatus.OPEN;
    }

    public Long getId() {
        return id;
    }

    public CompeteSeason getSeason() {
        return season;
    }

    public void setSeason(CompeteSeason season) {
        this.season = season;
    }

    public int getWeekIndex() {
        return weekIndex;
    }

    public void setWeekIndex(int weekIndex) {
        this.weekIndex = weekIndex;
    }

    public Instant getWeekStartUtc() {
        return weekStartUtc;
    }

    public void setWeekStartUtc(Instant weekStartUtc) {
        this.weekStartUtc = weekStartUtc;
    }

    public Instant getWeekEndUtc() {
        return weekEndUtc;
    }

    public void setWeekEndUtc(Instant weekEndUtc) {
        this.weekEndUtc = weekEndUtc;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public CompeteWeekCloseStatus getCloseStatus() {
        return closeStatus;
    }

    public void setCloseStatus(CompeteWeekCloseStatus closeStatus) {
        this.closeStatus = closeStatus;
    }
}
