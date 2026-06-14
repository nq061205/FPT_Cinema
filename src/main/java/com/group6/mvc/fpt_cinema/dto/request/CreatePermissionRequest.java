package com.group6.mvc.fpt_cinema.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePermissionRequest {
    private String permissionCode;
    private String permissionName;
    private String description;
}
