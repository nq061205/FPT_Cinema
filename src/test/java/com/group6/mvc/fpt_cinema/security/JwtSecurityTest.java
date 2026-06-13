package com.group6.mvc.fpt_cinema.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import com.group6.mvc.fpt_cinema.entity.Role;
import com.group6.mvc.fpt_cinema.entity.User;
import com.group6.mvc.fpt_cinema.security.JwtService.GeneratedToken;

class JwtSecurityTest {

    private static final String SECRET =
            "ZnB0LWNpbmVtYS1kZXYtc2VjcmV0LWtleS0yMDI2LTA2LTExIQ==";

    @Test
    void missingSecretGeneratesTemporaryHs256Key() {
        SecretKey secretKey = new SecurityConfig().jwtSecretKey(" ");

        assertThat(secretKey.getAlgorithm()).isEqualTo("HmacSHA256");
        assertThat(secretKey.getEncoded()).hasSize(32);
    }

    @Test
    void generatedTokenCanBeValidatedAndConvertedToAuthorities() {
        SecurityConfig securityConfig = new SecurityConfig();
        SecretKey secretKey = securityConfig.jwtSecretKey(SECRET);
        JwtEncoder encoder = securityConfig.jwtEncoder(secretKey);
        JwtDecoder decoder = securityConfig.jwtDecoder(secretKey, "fpt-cinema-api");
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();
        JwtService jwtService = new JwtService(
                encoder,
                "fpt-cinema-api",
                Duration.ofHours(1));

        Role role = new Role(RoleIds.MANAGER, "manager");
        User user = new User();
        user.setId(10);
        user.setRole(role);
        user.setEmail("manager@cinema.test");

        GeneratedToken generatedToken = jwtService.generateAccessToken(
                user,
                List.of("MOVIE_READ", "REPORT_READ"));
        Jwt decodedJwt = decoder.decode(generatedToken.value());
        Authentication authentication = converter.convert(decodedJwt);

        assertThat(decodedJwt.getSubject()).isEqualTo("manager@cinema.test");
        assertThat(decodedJwt.getClaimAsString("role")).isEqualTo("manager");
        assertThat(decodedJwt.getClaimAsStringList("permissions"))
                .containsExactly("MOVIE_READ", "REPORT_READ");
        assertThat(authentication.getAuthorities())
                .extracting("authority")
                .contains(
                        "ROLE_MANAGER",
                        "MOVIE_READ",
                        "REPORT_READ");
    }
}
