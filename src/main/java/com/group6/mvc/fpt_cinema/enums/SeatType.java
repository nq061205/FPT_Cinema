package com.group6.mvc.fpt_cinema.enums;

import java.util.Arrays;

public enum SeatType {
    NORMAL,
    VIP,
    COUPLE,
    PREMIUM;

    public static boolean isValid(String value) {
        if (value == null) return false;
        return Arrays.stream(values())
                .anyMatch(t -> t.name().equalsIgnoreCase(value));
    }

    public static SeatType fromString(String value) {
        return Arrays.stream(values())
                .filter(t -> t.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}
