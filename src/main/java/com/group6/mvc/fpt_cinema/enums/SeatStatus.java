package com.group6.mvc.fpt_cinema.enums;

import java.util.Arrays;

public enum SeatStatus {
    ACTIVE,
    LOCKED,
    BROKEN;

    public static boolean isValid(String value) {
        if (value == null) return false;
        return Arrays.stream(values())
                .anyMatch(s -> s.name().equalsIgnoreCase(value));
    }

    public static SeatStatus fromString(String value) {
        return Arrays.stream(values())
                .filter(s -> s.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}
