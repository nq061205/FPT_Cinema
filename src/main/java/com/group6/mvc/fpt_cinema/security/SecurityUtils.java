package com.group6.mvc.fpt_cinema.security;

import org.springframework.security.oauth2.jwt.Jwt;

import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.exception.AppException;

public class SecurityUtils {

    public static Integer getUserId(Jwt jwt) {
        return getIntegerClaim(jwt, "userId");
    }

    public static Integer getIntegerClaim(Jwt jwt, String claimName) {
        if (jwt == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Object value = jwt.getClaim(claimName);
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value != null) {
            try {
                return Integer.valueOf(value.toString());
            } catch (NumberFormatException ignored) {
            }
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }
}
