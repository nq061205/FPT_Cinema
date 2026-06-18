package com.group6.mvc.fpt_cinema.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "INVALID_CREDENTIALS")
    @Email(message = "INVALID_CREDENTIALS")
    private String email;

    @NotBlank(message = "INVALID_CREDENTIALS")
    private String password;
}
