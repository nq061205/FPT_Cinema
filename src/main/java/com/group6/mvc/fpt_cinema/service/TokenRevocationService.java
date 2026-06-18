package com.group6.mvc.fpt_cinema.service;

import java.time.Instant;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group6.mvc.fpt_cinema.entity.RevokedToken;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.repository.RevokedTokenRepository;

@Service
public class TokenRevocationService {

    private final RevokedTokenRepository revokedTokenRepository;

    public TokenRevocationService(RevokedTokenRepository revokedTokenRepository) {
        this.revokedTokenRepository = revokedTokenRepository;
    }

    @Transactional
    public void revoke(Jwt jwt) {
        if (jwt == null || jwt.getId() == null || jwt.getExpiresAt() == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Instant now = Instant.now();
        revokedTokenRepository.deleteByExpiresAtBefore(now);
        revokedTokenRepository.save(new RevokedToken(jwt.getId(), jwt.getExpiresAt(), now));
    }

    @Transactional(readOnly = true)
    public boolean isRevoked(Jwt jwt) {
        return jwt.getId() != null && revokedTokenRepository.existsById(jwt.getId());
    }
}
