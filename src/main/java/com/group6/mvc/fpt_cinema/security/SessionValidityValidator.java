package com.group6.mvc.fpt_cinema.security;

import java.time.Instant;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.group6.mvc.fpt_cinema.repository.UserRepository;

/**
 * Từ chối các access token được phát trước mốc {@code tokensValidFrom} của người
 * dùng. Khi người dùng đổi mật khẩu, mốc này được đặt bằng thời điểm hiện tại nên
 * toàn bộ phiên cũ (gồm cả phiên đang thao tác) đều bị vô hiệu hóa.
 */
@Component
public class SessionValidityValidator implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error INVALIDATED_TOKEN_ERROR =
            new OAuth2Error("invalid_token", "Session has been invalidated", null);

    private final UserRepository userRepository;

    public SessionValidityValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        Object userIdClaim = jwt.getClaim("userId");
        Instant issuedAt = jwt.getIssuedAt();

        if (!(userIdClaim instanceof Number) || issuedAt == null) {
            return OAuth2TokenValidatorResult.success();
        }

        Integer userId = ((Number) userIdClaim).intValue();
        Instant validFrom = userRepository.findTokensValidFromById(userId).orElse(null);

        // So sánh theo epoch-second: token phát cùng giây với thời điểm đổi mật khẩu
        // vẫn được chấp nhận (tránh loại nhầm phiên đăng nhập mới ngay sau đó).
        if (validFrom != null && issuedAt.getEpochSecond() < validFrom.getEpochSecond()) {
            return OAuth2TokenValidatorResult.failure(INVALIDATED_TOKEN_ERROR);
        }

        return OAuth2TokenValidatorResult.success();
    }
}
