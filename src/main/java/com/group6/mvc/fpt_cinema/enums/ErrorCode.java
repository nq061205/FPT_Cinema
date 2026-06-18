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
    MOVIE_NOT_FOUND(3009, "Movie not found", HttpStatus.NOT_FOUND),
    REVIEW_NOT_FOUND(3010,  "Review not found", HttpStatus.NOT_FOUND), 
    NOT_REVIEW_OWNER(3011, "You are not owner of review", HttpStatus.FORBIDDEN), 
    EDIT_TIME_EXPIRED(3012, "You can only edit within 24 hours", HttpStatus.BAD_REQUEST), 
    ROOM_NAME_BLANK(3013, "Room name must not be blank", HttpStatus.BAD_REQUEST),
    INVALID_ROOM_TYPE(3014, "Invalid room type", HttpStatus.BAD_REQUEST), 
    ROOM_NAME_EXIST(3015, "Room name already exists",HttpStatus.BAD_REQUEST), 
    ROOM_NOT_FOUND(3016, "Room not found", HttpStatus.NOT_FOUND),

    ROOM_HAS_ACTIVE_SHOWTIMES(3017, "Cannot close with active showtime", HttpStatus.BAD_REQUEST), 
    ROOM_NAME_TOO_LONG(4006, "Room name must not exceed 100 characters", HttpStatus.BAD_REQUEST),
    INVALID_ROOM_STATUS(4007, "Invalid room status", HttpStatus.BAD_REQUEST),
    SHOWTIME_OVERLAP(5001, "Showtime overlaps with an existing showtime in this room", HttpStatus.CONFLICT),
    SHOWTIME_NOT_FOUND(5002, "Showtime not found", HttpStatus.NOT_FOUND),
    SHOWTIME_IN_PAST(5003, "Cannot create showtime in the past", HttpStatus.BAD_REQUEST),
    INVALID_PRICE(5004, "Base price must be positive", HttpStatus.BAD_REQUEST),

    SEAT_NOT_FOUND(6001, "Seat not found", HttpStatus.NOT_FOUND),
    SEAT_ALREADY_EXISTS(6002, "Seat already exists in this room", HttpStatus.CONFLICT),
    INVALID_SEAT_TYPE(6003, "Invalid seat type. Must be NORMAL, VIP, COUPLE, or PREMIUM", HttpStatus.BAD_REQUEST),
    INVALID_SEAT_STATUS(6004, "Invalid seat status. Must be ACTIVE, LOCKED, or BROKEN", HttpStatus.BAD_REQUEST),
    SEAT_ROW_BLANK(6005, "Seat row must not be blank", HttpStatus.BAD_REQUEST),
    INVALID_SEAT_NUMBER(6006, "Seat number must be positive", HttpStatus.BAD_REQUEST),
    ROOM_NOT_ACTIVE_FOR_SEAT_UPDATE(6007, "Room must be ACTIVE to modify seats", HttpStatus.BAD_REQUEST),
    ROOM_HAS_EXISTING_SEATS(6008, "Room already has seats configured. Delete existing seats first", HttpStatus.BAD_REQUEST);
;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
