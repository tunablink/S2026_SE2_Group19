package com.example.cybersec.compete.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * A fixed-length competitive season (MVP: one open season; weeks roll inside it).
 */
@Entity
@Table(name = "compete_season")
public class CompeteSeason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String code;

    @Column(name = "starts_at_utc", nullable = false)
    private Instant startsAtUtc;

    @Column(name = "ends_at_utc", nullable = false)
    private Instant endsAtUtc;

    protected CompeteSeason() {
    }

    public CompeteSeason(String code, Instant startsAtUtc, Instant endsAtUtc) {
        this.code = code;
        this.startsAtUtc = startsAtUtc;
        this.endsAtUtc = endsAtUtc;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getStartsAtUtc() {
        return startsAtUtc;
    }

    public void setStartsAtUtc(Instant startsAtUtc) {
        this.startsAtUtc = startsAtUtc;
    }

    public Instant getEndsAtUtc() {
        return endsAtUtc;
    }

    public void setEndsAtUtc(Instant endsAtUtc) {
        this.endsAtUtc = endsAtUtc;
    }
}
