package com.group6.mvc.fpt_cinema.dto.response.showtime;

import java.math.BigDecimal;
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
public class ShowtimeResponse {

    private Integer id; 
    private Integer movieId; 
    private String movieTitle; 
    private Integer roomId; 
    private String roomName; 
    private LocalDateTime startTime; 
    private LocalDateTime endTime; 
    private BigDecimal basePrice; 
    private String status; 
    
}
