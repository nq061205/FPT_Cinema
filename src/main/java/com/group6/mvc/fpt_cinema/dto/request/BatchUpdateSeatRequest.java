package com.group6.mvc.fpt_cinema.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchUpdateSeatRequest {
    private List<Integer> seatIds; 
    private String seatType; 
    private String status; 
    
}
