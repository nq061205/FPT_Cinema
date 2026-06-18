package com.group6.mvc.fpt_cinema.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePermissionRequest {

    @NotBlank(message = "INVALID_PERMISSION_DATA")
    @Size(max = 100, message = "INVALID_PERMISSION_DATA")
    private String permissionCode;

    @NotBlank(message = "INVALID_PERMISSION_DATA")
    @Size(max = 150, message = "INVALID_PERMISSION_DATA")
    private String permissionName;

    @Size(max = 255, message = "INVALID_PERMISSION_DATA")
    private String description;
}
