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
    ROLE_NOT_FOUND(2001, "Role not found", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(2002, "User not found", HttpStatus.NOT_FOUND),
    PERMISSION_NOT_FOUND(2003, "Permission not found", HttpStatus.NOT_FOUND);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
