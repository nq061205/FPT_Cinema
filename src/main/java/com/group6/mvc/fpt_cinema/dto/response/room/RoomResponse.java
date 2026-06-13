package com.group6.mvc.fpt_cinema.dto.response.room;


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
public class RoomResponse {
    private Integer id; 
    private String roomName; 
    private String roomType; 
    private String status; 
    private Integer seatCount; 
    private LocalDateTime createdAt; 
    private LocalDateTime updatedAt; 

    
}
