package com.group6.mvc.fpt_cinema.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Server error"),

    CREATE_USER_FAIL(1001, "Create user failed"),
    EMAIL_EXIST(1002, "Email already exist"),
    ROLE_NOT_FOUND(2001, "Role not found"), PHONE_EXIST(1003, "Phone number already exist"),;

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}