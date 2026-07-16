package com.group6.mvc.fpt_cinema.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplaceRolePermissionsRequest {

    @NotNull(message = "INVALID_INPUT")
    private List<Integer> permissionIds;
}
