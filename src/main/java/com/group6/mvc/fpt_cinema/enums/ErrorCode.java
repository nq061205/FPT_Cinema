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
    INVALID_USER_DATA(1009, "Invalid user data", HttpStatus.BAD_REQUEST),
    PERMISSION_CODE_EXIST(1010, "Permission code already exists", HttpStatus.CONFLICT),
    INVALID_PERMISSION_DATA(1011, "Invalid permission data", HttpStatus.BAD_REQUEST),
    INVALID_INPUT(1012, "Invalid input data", HttpStatus.BAD_REQUEST),
    INVALID_OLD_PASSWORD(1013, "Old password is incorrect", HttpStatus.BAD_REQUEST),
    PASSWORD_CONFIRM_NOT_MATCH(1014, "Password confirmation does not match", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_SAME_AS_OLD(1015, "New password cannot be the same as old password", HttpStatus.BAD_REQUEST),

    ROLE_NOT_FOUND(2001, "Role not found", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(2002, "User not found", HttpStatus.NOT_FOUND),
    PERMISSION_NOT_FOUND(2003, "Permission not found", HttpStatus.NOT_FOUND),
    MOVIE_NOT_FOUND(2004, "Movie not found", HttpStatus.NOT_FOUND),
    REVIEW_NOT_FOUND(2005, "Review not found", HttpStatus.NOT_FOUND),
    BOOKING_NOT_FOUND(2006, "Booking is not found", HttpStatus.NOT_FOUND),

    CHAT_CONVERSATION_NOT_FOUND(3001, "Conversation not found", HttpStatus.NOT_FOUND),
    CHAT_CONVERSATION_CLOSED(3002, "Conversation is closed", HttpStatus.CONFLICT),
    INVALID_CHAT_MESSAGE(3003, "Chat message is invalid", HttpStatus.BAD_REQUEST),
    CHAT_SERVICE_UNAVAILABLE(3004, "Chat service is unavailable", HttpStatus.BAD_GATEWAY),

    INVALID_RATING(3101, "Invalid rating", HttpStatus.BAD_REQUEST),
    BOOKING_NOT_CONFIRMED(3102, "Booking is not confirmed", HttpStatus.FORBIDDEN),
    TICKET_NOT_USED(3103, "Ticket haven't used", HttpStatus.FORBIDDEN),
    MOVIE_MISMATCH(3104, "Movie mismatch", HttpStatus.BAD_REQUEST),
    SHOWTIME_NOT_PASSED(3105, "Showtime is not passed", HttpStatus.BAD_REQUEST),
    ALREADY_REVIEW(3106, "You already have reviewed film", HttpStatus.BAD_REQUEST),
    REVIEW_LIMIT_ACCESS(3107, "You exceed the limit review for a film", HttpStatus.BAD_REQUEST),
    NOT_REVIEW_OWNER(3108, "You are not owner of review", HttpStatus.FORBIDDEN),
    EDIT_TIME_EXPIRED(3109, "You can only edit within 24 hours", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
