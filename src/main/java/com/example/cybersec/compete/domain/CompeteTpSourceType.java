package com.example.cybersec.compete.domain;

/**
 * Maps to existing product flows: quiz submission ({@link #QUIZ_MODULE}) and lab validation ({@link #LAB}).
 * There is no separate Challenge JPA entity; {@code sourceKey} holds {@code module:{id}} or {@code lab:{type}}.
 */
public enum CompeteTpSourceType {
    QUIZ_MODULE,
    LAB
}
