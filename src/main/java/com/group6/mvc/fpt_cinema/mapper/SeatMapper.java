package com.group6.mvc.fpt_cinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.group6.mvc.fpt_cinema.dto.response.SeatResponse;
import com.group6.mvc.fpt_cinema.entity.Seat;

@Mapper(componentModel = "spring")
public interface SeatMapper {
    
    @Mapping(target = "roomId", source = "room.id")
    SeatResponse toResponse(Seat seat);

    
} 
