package com.group6.mvc.fpt_cinema.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.group6.mvc.fpt_cinema.service.TokenRevocationService;

@Component
public class RevokedTokenValidator implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error REVOKED_TOKEN_ERROR =
            new OAuth2Error("invalid_token", "Token has been revoked", null);

    private final TokenRevocationService tokenRevocationService;

    public RevokedTokenValidator(TokenRevocationService tokenRevocationService) {
        this.tokenRevocationService = tokenRevocationService;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if (tokenRevocationService.isRevoked(jwt)) {
            return OAuth2TokenValidatorResult.failure(REVOKED_TOKEN_ERROR);
        }
        return OAuth2TokenValidatorResult.success();
    }
}
