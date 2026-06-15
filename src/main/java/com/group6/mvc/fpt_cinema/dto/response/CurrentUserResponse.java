package com.group6.mvc.fpt_cinema.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CurrentUserResponse {
    private Integer userId;
    private String email;
    private Integer roleId;
    private String role;
    private List<String> permissions;
}
