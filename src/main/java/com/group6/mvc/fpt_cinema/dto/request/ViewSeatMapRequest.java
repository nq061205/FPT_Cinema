package com.group6.mvc.fpt_cinema.dto.request;

import com.group6.mvc.fpt_cinema.entity.Room;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViewSeatMapRequest {
    private Integer roomId; 
}
