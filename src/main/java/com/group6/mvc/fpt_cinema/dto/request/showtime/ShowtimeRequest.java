package com.group6.mvc.fpt_cinema.dto.request.showtime;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class ShowtimeRequest {
    private Integer movieId; 
    private Integer roomId; 
    private LocalDateTime startTime; 
    private BigDecimal basePrice; 

    
}