package com.group6.mvc.fpt_cinema.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileEditRequest {

    @NotBlank(message = "INVALID_USER_DATA")
    @Size(max = 100, message = "INVALID_USER_DATA")
    private String fullName;

    @NotBlank(message = "INVALID_USER_DATA")
    @Pattern(regexp = "^[0-9]{9,15}$", message = "INVALID_USER_DATA")
    private String phone;
}
