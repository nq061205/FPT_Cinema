package com.group6.mvc.fpt_cinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.group6.mvc.fpt_cinema.dto.request.ShowtimeRequest;
import com.group6.mvc.fpt_cinema.dto.response.ShowtimeResponse;
import com.group6.mvc.fpt_cinema.entity.Showtime;

@Mapper(componentModel = "spring")
public interface ShowtimeMapper {

    @Mapping(target = "movieId", source = "movie.id")
    @Mapping(target = "movieTitle", source = "movie.title")
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "roomName", source = "room.roomName")
    @Mapping(target = "endTime", ignore = true)
    ShowtimeResponse toResponse(Showtime showtime);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "movie", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "status",constant = "OPEN")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Showtime toEntity(ShowtimeRequest request);


}