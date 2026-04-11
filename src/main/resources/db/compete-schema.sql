-- Compete MVP — reference DDL for MySQL (tables are also created/updated by JPA ddl-auto).
-- Use this file for DBA review or manual installs when ddl-auto is off.

CREATE TABLE IF NOT EXISTS compete_season (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(32) NOT NULL UNIQUE,
    starts_at_utc DATETIME(6) NOT NULL,
    ends_at_utc DATETIME(6) NOT NULL
);

CREATE TABLE IF NOT EXISTS compete_week (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    season_id BIGINT NOT NULL,
    week_index INT NOT NULL,
    week_start_utc DATETIME(6) NOT NULL UNIQUE,
    week_end_utc DATETIME(6) NOT NULL,
    frozen TINYINT(1) NOT NULL DEFAULT 0,
    close_status VARCHAR(16) NOT NULL DEFAULT 'OPEN',
    CONSTRAINT fk_week_season FOREIGN KEY (season_id) REFERENCES compete_season (id)
);

CREATE TABLE IF NOT EXISTS user_compete_profile (
    user_id BIGINT NOT NULL PRIMARY KEY,
    timezone VARCHAR(64) NOT NULL DEFAULT 'UTC',
    timezone_band VARCHAR(8) NOT NULL DEFAULT 'AM2',
    bracket VARCHAR(16) NOT NULL DEFAULT 'CORE',
    use_alias TINYINT(1) NOT NULL DEFAULT 0,
    show_global_board TINYINT(1) NOT NULL DEFAULT 0,
    auto_enroll TINYINT(1) NOT NULL DEFAULT 1,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_compete_profile_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS tournament_enrollment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    week_id BIGINT NOT NULL,
    bracket VARCHAR(16) NOT NULL,
    timezone_band VARCHAR(8) NOT NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'ACTIVE',
    tp_total INT NOT NULL DEFAULT 0,
    opted_in_leaderboard TINYINT(1) NOT NULL DEFAULT 0,
    first_tp_at DATETIME(6),
    created_at DATETIME(6) NOT NULL,
    UNIQUE KEY uk_enrollment_user_week (user_id, week_id),
    CONSTRAINT fk_enrollment_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_enrollment_week FOREIGN KEY (week_id) REFERENCES compete_week (id)
);

CREATE INDEX IF NOT EXISTS idx_enrollment_week_bracket_tp ON tournament_enrollment (week_id, bracket, timezone_band, tp_total DESC);

CREATE TABLE IF NOT EXISTS tournament_point_event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    week_id BIGINT NOT NULL,
    source_type VARCHAR(24) NOT NULL,
    source_key VARCHAR(160) NOT NULL,
    delta_tp INT NOT NULL,
    idempotency_key VARCHAR(128) NOT NULL UNIQUE,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_tpe_enrollment FOREIGN KEY (enrollment_id) REFERENCES tournament_enrollment (id),
    CONSTRAINT fk_tpe_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_tpe_week FOREIGN KEY (week_id) REFERENCES compete_week (id)
);

CREATE INDEX IF NOT EXISTS idx_tpe_user_week ON tournament_point_event (user_id, week_id);

CREATE TABLE IF NOT EXISTS weekly_standing (
    enrollment_id BIGINT NOT NULL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    week_id BIGINT NOT NULL,
    bracket VARCHAR(16) NOT NULL,
    timezone_band VARCHAR(8) NOT NULL,
    tp_total INT NOT NULL,
    rank_in_cohort INT NOT NULL,
    cohort_size INT NOT NULL,
    percentile NUMERIC(8,4) NOT NULL,
    placement_points INT NOT NULL,
    computed_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_standing_enrollment FOREIGN KEY (enrollment_id) REFERENCES tournament_enrollment (id)
);

CREATE TABLE IF NOT EXISTS season_standing (
    user_id BIGINT NOT NULL,
    season_id BIGINT NOT NULL,
    sp_total INT NOT NULL DEFAULT 0,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (user_id, season_id),
    CONSTRAINT fk_ss_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_ss_season FOREIGN KEY (season_id) REFERENCES compete_season (id)
);

CREATE TABLE IF NOT EXISTS reward_grant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    week_id BIGINT,
    season_id BIGINT,
    tier VARCHAR(32) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    expires_at DATETIME(6),
    claimed_at DATETIME(6),
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_reward_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_reward_week FOREIGN KEY (week_id) REFERENCES compete_week (id),
    CONSTRAINT fk_reward_season FOREIGN KEY (season_id) REFERENCES compete_season (id)
);

CREATE INDEX IF NOT EXISTS idx_reward_user_status ON reward_grant (user_id, status);

CREATE TABLE IF NOT EXISTS quiz_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    module_id BIGINT NOT NULL,
    question_key VARCHAR(16) NOT NULL,
    correct_option VARCHAR(4) NOT NULL,
    sort_order INT NOT NULL,
    UNIQUE KEY uk_quiz_question_module_key (module_id, question_key),
    CONSTRAINT fk_quiz_question_module FOREIGN KEY (module_id) REFERENCES modules (id)
);

CREATE TABLE IF NOT EXISTS quiz_attempts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    module_id BIGINT NOT NULL,
    score INT NOT NULL,
    correct_count INT NOT NULL,
    total_questions INT NOT NULL,
    passed TINYINT(1) NOT NULL,
    submitted_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_quiz_attempt_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_quiz_attempt_module FOREIGN KEY (module_id) REFERENCES modules (id)
);

CREATE INDEX IF NOT EXISTS idx_quiz_attempt_user_module ON quiz_attempts (user_id, module_id);

CREATE TABLE IF NOT EXISTS quiz_attempt_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id BIGINT NOT NULL,
    question_key VARCHAR(16) NOT NULL,
    selected_option VARCHAR(4),
    correct_option VARCHAR(4) NOT NULL,
    correct TINYINT(1) NOT NULL,
    CONSTRAINT fk_quiz_attempt_answer_attempt FOREIGN KEY (attempt_id) REFERENCES quiz_attempts (id)
);
