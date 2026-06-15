package com.group6.mvc.fpt_cinema.dto.request;

import jakarta.validation.constraints.Email;
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
public class UpdateUserRequest {

    @Size(max = 100, message = "INVALID_USER_DATA")
    private String fullName;

    @Email(message = "INVALID_USER_DATA")
    @Size(max = 255, message = "INVALID_USER_DATA")
    private String email;

    @Pattern(regexp = "^[0-9]{9,15}$", message = "INVALID_USER_DATA")
    private String phone;

    @Size(min = 8, max = 100, message = "INVALID_USER_DATA")
    private String password;

    private String status;

    private Integer roleId;
}
