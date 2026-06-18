package com.group6.mvc.fpt_cinema.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.request.LoginRequest;
import com.group6.mvc.fpt_cinema.dto.request.RegisterRequest;
import com.group6.mvc.fpt_cinema.dto.response.CurrentUserResponse;
import com.group6.mvc.fpt_cinema.dto.response.LoginResponse;
import com.group6.mvc.fpt_cinema.dto.response.RegisterResponse;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.service.TokenRevocationService;
import com.group6.mvc.fpt_cinema.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final UserService userService;
    private final TokenRevocationService tokenRevocationService;

    public AuthenticationController(
            UserService userService,
            TokenRevocationService tokenRevocationService) {
        this.userService = userService;
        this.tokenRevocationService = tokenRevocationService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.<LoginResponse>builder()
                .message("Login successful")
                .result(userService.login(request))
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserResponse> me(@AuthenticationPrincipal Jwt jwt) {
        CurrentUserResponse currentUser = CurrentUserResponse.builder()
                .userId(getIntegerClaim(jwt, "userId"))
                .email(jwt.getSubject())
                .roleId(getIntegerClaim(jwt, "roleId"))
                .role(jwt.getClaimAsString("role"))
                .permissions(jwt.getClaimAsStringList("permissions"))
                .build();

        return ApiResponse.<CurrentUserResponse>builder()
                .message("Authenticated user")
                .result(currentUser)
                .build();
    }

    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.<RegisterResponse>builder()
                .message("Registration successful")
                .result(userService.register(request))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal Jwt jwt) {
        tokenRevocationService.revoke(jwt);
        return ApiResponse.<Void>builder()
                .message("Logout successful")
                .build();
    }

    private Integer getIntegerClaim(Jwt jwt, String claimName) {
        if (jwt == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Object value = jwt.getClaim(claimName);
        if (value instanceof Number number) {
            return number.intValue();
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }
}
