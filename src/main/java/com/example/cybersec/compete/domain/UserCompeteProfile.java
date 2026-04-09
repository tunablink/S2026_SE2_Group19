package com.example.cybersec.compete.domain;

import com.example.cybersec.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Per-user Compete preferences and cohort band. One row per {@link User}.
 */
@Entity
@Table(name = "user_compete_profile")
public class UserCompeteProfile {

    @Id
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 64)
    private String timezone = "UTC";

    @Column(name = "timezone_band", nullable = false, length = 8)
    private String timezoneBand = "AM2";

    @Column(name = "use_alias", nullable = false)
    private boolean useAlias;

    @Column(name = "show_global_board", nullable = false)
    private boolean showGlobalBoard;

    @Column(name = "auto_enroll", nullable = false)
    private boolean autoEnroll = true;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private CompeteBracket bracket = CompeteBracket.CORE;

    protected UserCompeteProfile() {
    }

    public UserCompeteProfile(User user) {
        this.user = user;
        this.userId = user.getId();
    }

    public Long getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getId();
        }
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getTimezoneBand() {
        return timezoneBand;
    }

    public void setTimezoneBand(String timezoneBand) {
        this.timezoneBand = timezoneBand;
    }

    public boolean isUseAlias() {
        return useAlias;
    }

    public void setUseAlias(boolean useAlias) {
        this.useAlias = useAlias;
    }

    public boolean isShowGlobalBoard() {
        return showGlobalBoard;
    }

    public void setShowGlobalBoard(boolean showGlobalBoard) {
        this.showGlobalBoard = showGlobalBoard;
    }

    public boolean isAutoEnroll() {
        return autoEnroll;
    }

    public void setAutoEnroll(boolean autoEnroll) {
        this.autoEnroll = autoEnroll;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public CompeteBracket getBracket() {
        return bracket;
    }

    public void setBracket(CompeteBracket bracket) {
        this.bracket = bracket;
    }
}
