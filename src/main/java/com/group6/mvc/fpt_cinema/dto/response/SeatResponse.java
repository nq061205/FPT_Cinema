package com.group6.mvc.fpt_cinema.dto.response;

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
public class SeatResponse {
    private Integer id; 
    private Integer roomId; 
    private String seatRow; 
    private Integer seatNumber; 
    private String seatType; 
    private String status; 
}
