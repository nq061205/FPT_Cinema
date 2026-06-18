package com.group6.mvc.fpt_cinema.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserPermissionResponse {
    private Integer id;
    private Integer userId;
    private String email;
    private Integer permissionId;
    private String permissionCode;
    private Boolean isGranted;
}
