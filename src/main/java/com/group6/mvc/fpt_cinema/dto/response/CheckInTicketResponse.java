package com.group6.mvc.fpt_cinema.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckInTicketResponse {
    private String ticketCode;
    private String movieTitle;
    private String roomName;
    private String seatRow;
    private Integer seatNumber;
    private LocalDateTime startTime;
    private LocalDateTime checkedInAt;
    private String status;
}
