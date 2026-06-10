package com.group6.mvc.fpt_cinema.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Server error"),


    USERNAME_NOT_BLANK(1001, "Username must not be blank"),
    EMAIL_NOT_BLANK(1002, "Email address must not be blank"),
    PASSWORD_NOT_BLANK(1003, "Password must not be blank");


    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}