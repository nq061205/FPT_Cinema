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


    CHAT_CONVERSATION_NOT_FOUND(3001, "Conversation not found", HttpStatus.NOT_FOUND),
    CHAT_CONVERSATION_CLOSED(3002, "Conversation is closed", HttpStatus.CONFLICT),
    INVALID_CHAT_MESSAGE(3003, "Chat message is invalid", HttpStatus.BAD_REQUEST),
    CHAT_SERVICE_UNAVAILABLE(3004, "Chat service is unavailable", HttpStatus.BAD_GATEWAY),

    INVALID_RATING(3101, "Invalid rating", HttpStatus.BAD_REQUEST),
    BOOKING_NOT_FOUND(3102, "Booking is not found", HttpStatus.NOT_FOUND),
    BOOKING_NOT_CONFIRMED(3103, "Booking is not confirmed", HttpStatus.FORBIDDEN),
    TICKET_NOT_USED(3104, "Ticket haven't used", HttpStatus.FORBIDDEN),
    MOVIE_MISMATCH(3105, "Movie mismatch", HttpStatus.BAD_REQUEST),
    SHOWTIME_NOT_PASSED(3106, "Showtime is not passed", HttpStatus.BAD_REQUEST),
    ALREADY_REVIEW(3107, "You already have reviewed film", HttpStatus.BAD_REQUEST),
    REVIEW_LIMIT_ACCESS(3108, "You exceed the limit review for a film", HttpStatus.BAD_REQUEST),
    MOVIE_NOT_FOUND(3109, "Movie not found", HttpStatus.NOT_FOUND),
    REVIEW_NOT_FOUND(3110,  "Review not found", HttpStatus.NOT_FOUND),
    NOT_REVIEW_OWNER(3111, "You are not owner of review", HttpStatus.FORBIDDEN),
    EDIT_TIME_EXPIRED(3112, "You can only edit within 24 hours", HttpStatus.BAD_REQUEST),
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
    ROOM_HAS_EXISTING_SEATS(6008, "Room already has seats configured. Delete existing seats first", HttpStatus.BAD_REQUEST),

    PROMOTION_NOT_FOUND(7001, "Promotion code not found", HttpStatus.NOT_FOUND),
    PROMOTION_INACTIVE(7002, "Promotion is not active", HttpStatus.BAD_REQUEST),
    PROMOTION_EXPIRED(7003, "Promotion has expired", HttpStatus.BAD_REQUEST),
    PROMOTION_NOT_STARTED(7004, "Promotion is not yet available", HttpStatus.BAD_REQUEST),
    PROMOTION_ALREADY_USED(7005, "You have already used this promotion", HttpStatus.BAD_REQUEST),
    INVALID_SUBTOTAL(7006, "Subtotal must be greater than zero", HttpStatus.BAD_REQUEST),

    PRODUCT_NOT_FOUND(8001, "Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_INACTIVE(8002, "Product is not active", HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_TYPE(8003, "Invalid product type. Must be FOOD, BEVERAGE, or COMBO", HttpStatus.BAD_REQUEST),
    INVALID_QUANTITY(8004, "Quantity must be at least 1", HttpStatus.BAD_REQUEST),

    SHOWTIME_NOT_BOOKABLE(9001, "Showtime is not open for booking", HttpStatus.BAD_REQUEST),
    NO_SEATS_SELECTED(9002, "At least one seat must be selected", HttpStatus.BAD_REQUEST),
    SEAT_ALREADY_BOOKED(9003, "One or more seats are already booked", HttpStatus.CONFLICT),
    SEAT_NOT_IN_SHOWTIME_ROOM(9004, "One or more seats do not belong to the showtime's room", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
