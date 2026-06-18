package com.group6.mvc.fpt_cinema.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PermissionResponse {
    private Integer id;
    private String permissionCode;
    private String permissionName;
    private String description;
}
