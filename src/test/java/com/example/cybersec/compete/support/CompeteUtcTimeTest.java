package com.example.cybersec.compete.support;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class CompeteUtcTimeTest {

    @Test
    void startOfWeekMondayUtc_alignsToUtcMonday() {
        Instant wednesday = Instant.parse("2026-04-08T15:00:00Z");
        Instant monday = CompeteUtcTime.startOfWeekMondayUtc(wednesday);
        assertThat(monday).isEqualTo("2026-04-06T00:00:00Z");
        Instant end = CompeteUtcTime.endOfWeekSundayUtc(monday);
        assertThat(end).isBefore(monday.plus(7, ChronoUnit.DAYS));
        assertThat(end).isAfter(monday.plus(6, ChronoUnit.DAYS));
    }
}
