package com.group6.mvc.fpt_cinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.group6.mvc.fpt_cinema.dto.request.room.RoomRequest;
import com.group6.mvc.fpt_cinema.dto.response.room.RoomResponse;
import com.group6.mvc.fpt_cinema.entity.Room;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    
    @Mapping(target = "seatCount", ignore = true)
    RoomResponse toResponse(Room room); 

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Room toEntity(RoomRequest request);


    
}
