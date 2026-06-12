package com.group6.mvc.fpt_cinema.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String accessToken;

    @Builder.Default
    private String tokenType = "Bearer";

    private long expiresIn;
    private Integer userId;
    private String fullName;
    private String email;
    private Integer roleId;
    private String role;
    private List<String> permissions;
}
