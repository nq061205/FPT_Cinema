package com.group6.mvc.fpt_cinema.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Server error"),

    USERNAME_NOT_BLANK(1001, "Username must not be blank"),
    EMAIL_NOT_BLANK(1002, "Email address must not be blank"),
    PASSWORD_NOT_BLANK(1003, "Password must not be blank"),

    ROLE_NOT_FOUND(2001, "Role not found"),
    INVALID_RATING(3001, "Invalid rating"),
    BOOKING_NOT_FOUND(3002, "Booking is not found"),
    BOOKING_NOT_CONFIRMED(3003, "Booking is not confirmed"),
    TICKET_NOT_USED(3004, "Ticket haven't used"),
    MOVIE_MISMATCH(3005, "Movie mismatch"),
    SHOWTIME_NOT_PASSED(3006, "Showtime is not passed"),
    ALREADY_REVIEW(3007, "You already have reviewed film"),
    REVIEW_LIMIT_ACCESS(3008, "You exceed the limit review for a film"),
    MOVIE_NOT_FOUND(3009, "Movie not found");
    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}