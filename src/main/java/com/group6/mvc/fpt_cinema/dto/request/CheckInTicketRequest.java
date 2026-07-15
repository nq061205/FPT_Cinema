package com.group6.mvc.fpt_cinema.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckInTicketRequest {
    @NotBlank(message = "INVALID_INPUT")
    private String ticketCode;
}
