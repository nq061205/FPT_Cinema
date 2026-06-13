package com.group6.mvc.fpt_cinema.enums;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Server error", HttpStatus.INTERNAL_SERVER_ERROR),

    CREATE_USER_FAIL(1001, "Create user failed", HttpStatus.BAD_REQUEST),
    EMAIL_EXIST(1002, "Email already exists", HttpStatus.CONFLICT),
    PHONE_EXIST(1003, "Phone number already exists", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS(1004, "Invalid email or password", HttpStatus.UNAUTHORIZED),
    ACCOUNT_INACTIVE(1005, "Account is inactive", HttpStatus.FORBIDDEN),
    ACCOUNT_LOCKED(1006, "Account is locked", HttpStatus.FORBIDDEN),
    UNAUTHORIZED(1007, "Authentication is required", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(1008, "Access denied", HttpStatus.FORBIDDEN),
    ROLE_NOT_FOUND(2001, "Role not found", HttpStatus.NOT_FOUND),

    INVALID_RATING(3001, "Invalid rating", HttpStatus.BAD_REQUEST),
    BOOKING_NOT_FOUND(3002, "Booking is not found", HttpStatus.NOT_FOUND),
    BOOKING_NOT_CONFIRMED(3003, "Booking is not confirmed", HttpStatus.FORBIDDEN),
    TICKET_NOT_USED(3004, "Ticket haven't used", HttpStatus.FORBIDDEN),
    MOVIE_MISMATCH(3005, "Movie mismatch", HttpStatus.BAD_REQUEST),
    SHOWTIME_NOT_PASSED(3006, "Showtime is not passed", HttpStatus.BAD_REQUEST),
    ALREADY_REVIEW(3007, "You already have reviewed film", HttpStatus.BAD_REQUEST),
    REVIEW_LIMIT_ACCESS(3008, "You exceed the limit review for a film", HttpStatus.BAD_REQUEST),
    MOVIE_NOT_FOUND(3009, "Movie not found", HttpStatus.NOT_FOUND);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
